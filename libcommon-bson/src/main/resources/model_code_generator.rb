require 'yaml'


def fill_imports(code, super_class, fields)
  javas = []
  orgs = []
  coms = ["com.github.fmjsjx.libcommon.bson.model.#{super_class}"]
  javas << 'java.util.LinkedHashMap'
  javas << 'java.util.List'
  orgs << 'org.bson.BsonDocument'
  orgs << 'org.bson.Document'
  orgs << 'org.bson.conversions.Bson'
  coms << 'com.github.fmjsjx.libcommon.bson.BsonUtil'
  coms << 'com.mongodb.client.model.Updates'
  if super_class == 'ObjectModel'
    coms << 'com.github.fmjsjx.libcommon.bson.DotNotation'
  end
  if fields.any? { |field| field['type'] == 'datetime' }
    javas << 'java.time.LocalDateTime'
    javas << 'java.time.ZoneId'
    javas << 'java.util.Date'
  end
  if fields.any? { |field| field['type'] == 'int' }
    orgs << 'org.bson.BsonInt32'
  end
  if fields.any? { |field| field['type'] == 'long' }
    orgs << 'org.bson.BsonInt64'
  end
  if fields.any? { |field| field['type'] == 'double' }
    orgs << 'org.bson.BsonDouble'
  end
  if fields.any? { |field| field['type'] == 'string' }
    orgs << 'org.bson.BsonString'
    coms << 'com.github.fmjsjx.libcommon.util.StringUtil'
  end
  if fields.any? { |field| field['type'] == 'map' }
    coms << 'com.github.fmjsjx.libcommon.bson.model.DefaultMapModel'
  end
  if fields.any? { |field| field['type'] == 'simple-map' }
    coms << 'com.github.fmjsjx.libcommon.bson.model.SimpleMapModel'
  end
  javas.sort.each { |v| code << "import #{v};\n" }
  code << "\n"
  orgs.sort.each { |v| code << "import #{v};\n" }
  code << "\n"
  coms.sort.each { |v| code << "import #{v};\n" }
  code << "\n"
end

def tabs(n, value = '')
  ' ' * 4 * n + value
end

def fill_fields(code, cfg, parent=nil)
  name = cfg['name']
  fields = cfg['fields']
  unless parent.nil?
    code << tabs(1, "private final #{parent} parent;\n\n")
  end
  fields.each do |field|
    if field['json-ignore']
      code << tabs(1, "@com.fasterxml.jackson.annotation.JsonIgnore\n")
    end
    field_type = field['type']
    case field_type
    when 'object'
      code << tabs(1, "private final #{field['model']} #{field['name']} = new #{field['model']}(this);\n")
    when 'map'
      key_type = field['key']
      value_type = field['value']
      case key_type
      when 'string'
        code << tabs(1, "private final DefaultMapModel<String, #{value_type}, #{name}> #{field['name']} = DefaultMapModel.stringKeys(this, \"#{field['bname']}\", #{value_type}::of);\n")
      when 'int'
        code << tabs(1, "private final DefaultMapModel<Integer, #{value_type}, #{name}> #{field['name']} = DefaultMapModel.integerKeys(this, \"#{field['bname']}\", #{value_type}::of);\n")
      when 'long'
        code << tabs(1, "private final DefaultMapModel<Long, #{value_type}, #{name}> #{field['name']} = DefaultMapModel.longKeys(this, \"#{field['bname']}\", #{value_type}::of);\n")
      else
        raise "unsupported type `#{key_type}`"
      end
    when 'simple-map'
      key_type = field['key']
      value_type = boxed_jtype(field['value'])
      value_converter = value_converter(field['value'])
      case key_type
      when 'string'
        code << tabs(1, "private final SimpleMapModel<String, #{value_type}, #{name}> #{field['name']} = SimpleMapModel.stringKeys(this, \"#{field['bname']}\", #{value_converter});\n")
      when 'int'
        code << tabs(1, "private final SimpleMapModel<Integer, #{value_type}, #{name}> #{field['name']} = SimpleMapModel.integerKeys(this, \"#{field['bname']}\", #{value_converter});\n")
      when 'long'
        code << tabs(1, "private final SimpleMapModel<Long, #{value_type}, #{name}> #{field['name']} = SimpleMapModel.longKeys(this, \"#{field['bname']}\", #{value_converter});\n")
      else
        raise "unsupported type `#{key_type}`"
      end
    else
      code << tabs(1, "private #{jtype(field_type)} #{field['name']};\n")
    end
  end
  code << "\n"
end

# fill getter setters
def fill_xetters(code, cfg)
  # TODO
end

def jtype(value)
  case value
  when 'int'
    'int'
  when 'long'
    'long'
  when 'double'
    'double'
  when 'string'
    'String'
  when 'datetime'
    'LocalDateTime'
  else
    raise "unsupported type `#{value}`"
  end
end

def boxed_jtype(value)
  case value
  when 'int'
    'Integer'
  when 'long'
    'Long'
  when 'double'
    'Double'
  when 'string'
    'String'
  when 'datetime'
    'LocalDateTime'
  else
    raise "unsupported type `#{value}`"
  end
end

def value_converter(type)
  case type
  when 'int'
    'BsonInt32::new'
  when 'long'
    'BsonInt64::new'
  when 'double'
    'BsonDouble::new'
  when 'string'
    'BsonString::new'
  end
end

def generate_root(cfg)
  code = "package #{cfg['package']};\n\n"
  fill_imports(code, 'RootModel', cfg['fields'])
  code << "public class #{cfg['name']} extends RootModel<#{cfg['name']}> {\n\n"
  fill_fields(code, cfg)
  # TODO
  code << "}\n"
end

def generate_object(cfg)
  code = "package #{cfg['package']};\n\n"
  fill_imports(code, 'ObjectModel', cfg['fields'])
  code << "public class #{cfg['name']} extends ObjectModel<#{cfg['name']}> {\n\n"
  parent = cfg['parent']
  xpath = ["\"#{cfg['bname']}\""]
  p = parent
  until p['parent'].nil?
    xpath.insert(0, "\"#{p['bname']}\"")
    p = p['parent']
  end
  code << tabs(1, "private static final DotNotation XPATH = DotNotation.of(#{xpath.join(', ')});\n\n")
  fill_fields(code, cfg, parent['name'])
  code << tabs(1, "public #{cfg['name']}(#{parent['name']} parent) {\n")
  code << tabs(2, "this.parent = parent;\n")
  code << tabs(1, "}\n\n")
  # TODO
  code << "}\n"
end

def generate_map_value(cfg)
  code = "package #{cfg['package']};\n\n"
  fill_imports(code, 'DefaultMapValueModel', cfg['fields'])
  code << "public class #{cfg['name']} extends DefaultMapValueModel<#{boxed_jtype(cfg['key'])}, #{cfg['name']}> {\n\n"
  fill_fields(code, cfg)
  # TODO
  code << "}\n"
end

cfg = File.open(ARGV[0]) { |io| YAML.load io.read }

parents = Hash.new
bnames = Hash.new

cfg['objects'].each do |model|
  model['fields'].each do |field|
    if field['type'] == 'object'
      parents[field['model']] = model
      bnames[field['model']] = field['bname']
    end
  end
end

cfg['objects'].each do |model|
  unless model.has_key? 'package'
    model['package'] = cfg['package']
  end
  if parents.has_key? model['name']
    model['parent'] = parents[model['name']]
    model['bname'] = bnames[model['name']]
  end
  case model['type']
  when 'root'
    code = generate_root(model)
  when 'object'
    code = generate_object(model)
  when 'map-value'
    code = generate_map_value(model)
  else
    raise "unknown type #{model['type']}"
  end
  puts "#{code}"
  puts "---------------------------------------------"
end

puts "Done."
