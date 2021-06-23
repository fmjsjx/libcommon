require 'yaml'


def fill_imports(code, super_class, fields)
  javas = []
  orgs = []
  coms = ["com.github.fmjsjx.libcommon.bson.model.#{super_class}"]
  javas << 'java.util.LinkedHashMap'
  javas << 'java.util.List'
  javas << 'java.util.Map'
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
    coms << 'com.github.fmjsjx.libcommon.util.DateTimeUtil'
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
  if fields.any? { |field| field['type'] == 'bool' }
    orgs << 'org.bson.BsonBoolean'
  end
  if fields.any? { |field| field['type'] == 'string' }
    orgs << 'org.bson.BsonString'
  end
  if fields.any? { |field| %w(int long double bool).none? field['type']}
    coms << 'com.github.fmjsjx.libcommon.util.ObjectUtil'
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
  fields.select do |field|
    field['type'] == 'datetime' && !field['required'] && field.has_key?('default') && field['default'] != 'now'
  end.each do |field|
    default_value = field['default']
    code << tabs(1, "private static final LocalDateTime default#{camcel_name(field['name'])} = LocalDateTime.parse(\"#{default_value}\");\n\n")
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

def camcel_name(name)
  name[0].upcase << name[1..-1]
end

# fill getter setters
def fill_xetters(code, cfg)
  cfg['fields'].each_with_index do |field, index|
    name = field['name']
    camcel = camcel_name(name)
    type = field['type']
    case type
    when 'object'
      code << tabs(1, "public #{field['model']} get#{camcel}() {\n")
      code << tabs(2, "return #{name};\n")
      code << tabs(1, "}\n\n")
    when 'map'
      key_type = field['key']
      value_type = field['value']
      case key_type
      when 'string'
        code << tabs(1, "public DefaultMapModel<String, #{value_type}, #{cfg['name']}> get#{camcel}() {\n")
      when 'int'
        code << tabs(1, "public DefaultMapModel<Integer, #{value_type}, #{cfg['name']}> get#{camcel}() {\n")
      when 'long'
        code << tabs(1, "public DefaultMapModel<Long, #{value_type}, #{cfg['name']}> get#{camcel}() {\n")
      else
        raise "unsupported type `#{key_type}`"
      end
      code << tabs(2, "return #{name};\n")
      code << tabs(1, "}\n\n")
    when 'simple-map'
      key_type = field['key']
      value_type = boxed_jtype(field['value'])
      case key_type
      when 'string'
        code << tabs(1, "public SimpleMapModel<String, #{value_type}, #{cfg['name']}> get#{camcel}() {\n")
      when 'int'
        code << tabs(1, "public SimpleMapModel<Integer, #{value_type}, #{cfg['name']}> get#{camcel}() {\n")
      when 'long'
        code << tabs(1, "public SimpleMapModel<Long, #{value_type}, #{cfg['name']}> get#{camcel}() {\n")
      else
        raise "unsupported type `#{key_type}`"
      end
      code << tabs(2, "return #{name};\n")
      code << tabs(1, "}\n\n")
    else
      value_type = jtype(type)
      if type == 'bool'
        code << tabs(1, "public #{value_type} is#{camcel}() {\n")
      else
        code << tabs(1, "public #{value_type} get#{camcel}() {\n")
      end
      code << tabs(2, "return #{name};\n")
      code << tabs(1, "}\n\n")
      code << tabs(1, "public void set#{camcel}(#{value_type} #{name}) {\n")
      if %w(int long double bool).include? type
        code << tabs(2, "if (this.#{name} != #{name}) {\n")
      else
        code << tabs(2, "if (ObjectUtil.isNotEquals(this.#{name}, #{name})) {\n")
      end
      code << tabs(3, "this.#{name} = #{name};\n")
      code << tabs(3, "updatedFields.set(#{index + 1});\n")
      if cfg['type'] == 'map-value'
        code << tabs(3, "emitUpdated();\n")
      end
      code << tabs(2, "}\n")
      code << tabs(1, "}\n\n")
      if field['increase'] == true
        code << tabs(1, "public #{value_type} increase#{camcel}() {\n")
        code << tabs(2, "var #{name} = this.#{name} += 1;\n")
        code << tabs(2, "updatedFields.set(#{index + 1});\n")
        if cfg['type'] == 'map-value'
          code << tabs(2, "emitUpdated();\n")
        end
        code << tabs(2, "return #{name};\n")
        code << tabs(1, "}\n\n")
      end
    end
  end
end

def fill_to_bson(code, cfg, bson_type='BsonDocument')
  code << tabs(1, "@Override\n")
  code << tabs(1, "public #{bson_type} toBson() {\n")
  code << tabs(2, "var bson = new BsonDocument();\n")
  cfg['fields'].each do |field|
    name = field['name']
    bname = field['bname']
    type = field['type']
    case
    when %w(object map simple-map).include?(type) then
      code << tabs(2, "bson.append(\"#{bname}\", #{name}.toBson());\n")
    when type == 'int'
      code << tabs(2, "bson.append(\"#{bname}\", new BsonInt32(#{name}));\n")
    when type == 'long'
      code << tabs(2, "bson.append(\"#{bname}\", new BsonInt64(#{name}));\n")
    when type == 'double'
      code << tabs(2, "bson.append(\"#{bname}\", new BsonDouble(#{name}));\n")
    when type == 'bool'
      code << tabs(2, "bson.append(\"#{bname}\", new BsonBoolean(#{name}));\n")
    when type == 'string'
      code << tabs(2, "bson.append(\"#{bname}\", new BsonString(#{name}));\n")
    when type == 'datetime'
      if field['required']
        code << tabs(2, "bson.append(\"#{bname}\", BsonUtil.toBsonDateTime(#{name}));\n")
      else
        code << tabs(2, "if (#{name} != null) {\n")
        code << tabs(3, "bson.append(\"#{bname}\", BsonUtil.toBsonDateTime(#{name}));\n")
        code << tabs(2, "}\n")
      end
    end
  end
  code << tabs(2, "return bson;\n")
  code << tabs(1, "}\n\n")
end

def fill_to_document(code, cfg)
  code << tabs(1, "@Override\n")
  code << tabs(1, "public Document toDocument() {\n")
  code << tabs(2, "var doc = new Document();\n")
  cfg['fields'].each do |field|
    name = field['name']
    bname = field['bname']
    type = field['type']
    case
    when %w(object map simple-map).include?(type) then
      code << tabs(2, "doc.append(\"#{bname}\", #{name}.toDocument());\n")
    when type == 'datetime'
      if field['required']
        code << tabs(2, "doc.append(\"#{bname}\", DateTimeUtil.toLegacyDate(#{name}));\n")
      else
        code << tabs(2, "if (#{name} != null) {\n")
        code << tabs(3, "doc.append(\"#{bname}\", DateTimeUtil.toLegacyDate(#{name}));\n")
        code << tabs(2, "}\n")
      end
    else
      code << tabs(2, "doc.append(\"#{bname}\", #{name});\n")
    end
  end
  code << tabs(2, "return doc;\n")
  code << tabs(1, "}\n\n")
end

def fill_load(code, cfg)
  code << tabs(1, "@Override\n")
  code << tabs(1, "public void load(Document src) {\n")
  cfg['fields'].each do |field|
    name = field['name']
    bname = field['bname']
    type = field['type']
    case
    when %w(object map simple-map).include?(type) then
      code << tabs(2, "BsonUtil.documentValue(src, \"#{bname}\").ifPresent(#{name}::load);\n")
    when type == 'int'
      if field['required']
        code << tabs(2, "#{name} = BsonUtil.intValue(src, \"#{bname}\").getAsInt();\n")
      else
        default_value = field.has_key?('default') ? field['default'] : 0
        code << tabs(2, "#{name} = BsonUtil.intValue(src, \"#{bname}\").orElse(#{default_value});\n")
      end
    when type == 'long'
      if field['required']
        code << tabs(2, "#{name} = BsonUtil.longValue(src, \"#{bname}\").getAsLong();\n")
      else
        default_value = field.has_key?('default') ? field['default'] : 0
        code << tabs(2, "#{name} = BsonUtil.longValue(src, \"#{bname}\").orElse(#{default_value}L);\n")
      end
    when type == 'double'
      if field['required']
        code << tabs(2, "#{name} = BsonUtil.doubleValue(src, \"#{bname}\").getAsDouble();\n")
      else
        default_value = field.has_key?('default') ? field['default'] : 0
        code << tabs(2, "#{name} = BsonUtil.doubleValue(src, \"#{bname}\").orElse(#{default_value});\n")
      end
    when type == 'bool'
      if field['required']
        code << tabs(2, "#{name} = BsonUtil.<Boolean>embedded(src, \"#{bname}\").get().booleanValue();\n")
      else
        default_value = field.has_key?('default') ? field['default'].to_s == 'true' : false
        code << tabs(2, "#{name} = BsonUtil.<Boolean>embedded(src, \"#{bname}\").orElse(Boolean.#{default_value.to_s.upcase});\n")
      end
    when type == 'string'
      if field['required']
        code << tabs(2, "#{name} = BsonUtil.stringValue(src, \"#{bname}\").get();\n")
      else
        default_value = field.has_key?('default') ? field['default'] : ''
        code << tabs(2, "#{name} = BsonUtil.stringValue(src, \"#{bname}\").orElse(\"#{default_value}\");\n")
      end
    when type == 'datetime'
      if field['required']
        code << tabs(2, "#{name} = BsonUtil.dateTimeValue(src, \"#{bname}\").get();\n")
      else
        if field['default'].nil?
          code << tabs(2, "#{name} = BsonUtil.dateTimeValue(src, \"#{bname}\").orElse(null);\n")
        elsif field['default'] == 'now'
          code << tabs(2, "#{name} = BsonUtil.dateTimeValue(src, \"#{bname}\").orElseGet(LocalDateTime::now);\n")
        else
          default_value = "default#{camcel_name(field['name'])}"
          code << tabs(2, "#{name} = BsonUtil.dateTimeValue(src, \"#{bname}\").orElse(#{default_value});\n")
        end
      end
    end
  end
  if cfg['type'] == 'root'
    code << tabs(2, "reset();\n")
  end
  code << tabs(1, "}\n\n")
end

def fill_append_updates(code, cfg)
  code << tabs(1, "@Override\n")
  code << tabs(1, "protected void appendFieldUpdates(List<Bson> updates) {\n")
  code << tabs(2, "var updatedFields = this.updatedFields;\n")
  cfg['fields'].each_with_index do |field, index|
    name = field['name']
    bname = field['bname']
    type = field['type']
    if cfg['type'] == 'root'
      xpath = "\"#{bname}\""
    else
      xpath = "xpath().resolve(\"#{bname}\").value()"
    end
    case
    when %w(object map simple-map).include?(type) then
      code << tabs(2, "var #{name} = this.#{name};\n")
      code << tabs(2, "if (#{name}.updated()) {\n")
      code << tabs(3, "#{name}.appendUpdates(updates);\n")
      code << tabs(2, "}\n")
    when type == 'datetime'
      code << tabs(2, "if (updatedFields.get(#{index + 1})) {\n")
      code << tabs(3, "updates.add(Updates.set(#{xpath}, BsonUtil.toBsonDateTime(#{name})));\n")
      code << tabs(2, "}\n")
    else
      code << tabs(2, "if (updatedFields.get(#{index + 1})) {\n")
      code << tabs(3, "updates.add(Updates.set(#{xpath}, #{name}));\n")
      code << tabs(2, "}\n")
    end
  end
  code << tabs(1, "}\n\n")
end

def fill_reset_children(code, cfg)
  code << tabs(1, "@Override\n")
  code << tabs(1, "protected void resetChildren() {\n")
  cfg['fields'].select do |field|
    %w(object map simple-map).include?(field['type'])
  end.each do |field|
    name = field['name']
    code << tabs(2, "#{name}.reset();\n")
  end
  code << tabs(1, "}\n\n")
end

def fill_to_sub_update(code, cfg)
  code << tabs(1, "@Override\n")
  code << tabs(1, "public Object toSubUpdate() {\n")
  if cfg['fields'].count { |field| !field['json-ignore'] } > 0
    code << tabs(2, "var update = new LinkedHashMap<>();\n")
    code << tabs(2, "var updatedFields = this.updatedFields;\n")
    cfg['fields'].each_with_index do |field, index|
      unless field['json-ignore']
        name = field['name']
        if %w(object map simple-map).include?(field['type'])
          code << tabs(2, "if (#{name}.updated()) {\n")
          code << tabs(3, "update.put(\"#{name}\", #{name}.toUpdate());\n")
        else
          code << tabs(2, "if (updatedFields.get(#{index + 1})) {\n")
          code << tabs(3, "update.put(\"#{name}\", #{name});\n")
        end
        code << tabs(2, "}\n")
      end
    end
    code << tabs(2, "return update;\n")
  else
    code << tabs(2, "return Map.of();\n")
  end
  code << tabs(1, "}\n\n")
end

def fill_to_delete(code, cfg)
  code << tabs(1, "@Override\n")
  code << tabs(1, "public Map<Object, Object> toDelete() {\n")
  if cfg['fields'].any? { |field| %w(object map simple-map).include?(field['type']) }
    code << tabs(2, "var delete = new LinkedHashMap<>();\n")
    cfg['fields'].select do |field|
      %w(object map simple-map).include?(field['type'])
    end.each do |field|
      name = field['name']
      code << tabs(2, "var #{name} = this.#{name};\n")
      code << tabs(2, "if (#{name}.deletedSize() > 0) {\n")
      code << tabs(3, "delete.put(\"#{name}\", #{name}.toDelete());\n")
      code << tabs(2, "}\n")
    end
    code << tabs(2, "return delete;\n")
  else
    code << tabs(2, "return Map.of();\n")
  end
  code << tabs(1, "}\n\n")
end

def fill_deleted_size(code, cfg)
  code << tabs(1, "@Override\n")
  code << tabs(1, "protected int deletedSize() {\n")
  if cfg['fields'].any? { |field| %w(object map simple-map).include?(field['type']) }
    code << tabs(2, "var n = 0;\n")
    cfg['fields'].select do |field|
      %w(object map simple-map).include?(field['type'])
    end.each do |field|
      name = field['name']
      code << tabs(2, "if (#{name}.deletedSize() > 0) {\n")
      code << tabs(3, "n++;\n")
      code << tabs(2, "}\n")
    end
    code << tabs(2, "return n;\n")
  else
    code << tabs(2, "return 0;\n")
  end
  code << tabs(1, "}\n\n")
end

def jtype(value)
  case value
  when 'int'
    'int'
  when 'long'
    'long'
  when 'double'
    'double'
  when 'bool'
    'boolean'
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
  when 'bool'
    'Boolean'
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
  when 'bool'
    'BsonBoolean::new'
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
  fill_xetters(code, cfg)
  fill_to_bson(code, cfg)
  fill_to_document(code, cfg)
  fill_load(code, cfg)
  fill_append_updates(code, cfg)
  fill_reset_children(code, cfg)
  fill_to_sub_update(code, cfg)
  fill_to_delete(code, cfg)
  fill_deleted_size(code, cfg)
  code << "}\n"
end

def all_object(cfg)
  parent = cfg['parent']
  if parent.nil?
    return false
  end
  if parent['type'] == 'root'
    return true
  else
    return all_object(parent)
  end
end

def generate_object(cfg)
  code = "package #{cfg['package']};\n\n"
  fill_imports(code, 'ObjectModel', cfg['fields'])
  code << "public class #{cfg['name']} extends ObjectModel<#{cfg['name']}> {\n\n"
  static_xpath = all_object(cfg)
  if static_xpath
    parent = cfg['parent']
    xpath = ["\"#{cfg['bname']}\""]
    p = parent
    until p['parent'].nil?
      xpath.insert(0, "\"#{p['bname']}\"")
      p = p['parent']
    end
    code << tabs(1, "private static final DotNotation XPATH = DotNotation.of(#{xpath.join(', ')});\n\n")
  end
  fill_fields(code, cfg, parent['name'])
  code << tabs(1, "public #{cfg['name']}(#{parent['name']} parent) {\n")
  code << tabs(2, "this.parent = parent;\n")
  code << tabs(1, "}\n\n")
  fill_xetters(code, cfg)
  code << tabs(1, "@SuppressWarnings(\"unchecked\")\n")
  code << tabs(1, "@Override\n")
  code << tabs(1, "public #{parent['name']} parent() {\n")
  code << tabs(2, "return parent;\n")
  code << tabs(1, "}\n\n")
  code << tabs(1, "@Override\n")
  code << tabs(1, "public DotNotation xpath() {\n")
  if static_xpath
    code << tabs(2, "return XPATH;\n")
  else
    code << tabs(2, "return parent().xpath().resolve(\"#{bname}\");")
  end
  code << tabs(1, "}\n\n")
  fill_to_bson(code, cfg)
  fill_to_document(code, cfg)
  fill_load(code, cfg)
  fill_append_updates(code, cfg)
  fill_reset_children(code, cfg)
  fill_to_sub_update(code, cfg)
  fill_to_delete(code, cfg)
  fill_deleted_size(code, cfg)
  code << "}\n"
end

def generate_map_value(cfg)
  code = "package #{cfg['package']};\n\n"
  fill_imports(code, 'DefaultMapValueModel', cfg['fields'])
  code << "public class #{cfg['name']} extends DefaultMapValueModel<#{boxed_jtype(cfg['key'])}, #{cfg['name']}> {\n\n"
  code << tabs(1, "public static final #{cfg['name']} of(Document document) {\n")
  code << tabs(2, "var obj = new #{cfg['name']}();\n")
  code << tabs(2, "obj.load(document);\n")
  code << tabs(2, "return obj;\n")
  code << tabs(1, "}\n\n")
  fill_fields(code, cfg)
  fill_xetters(code, cfg)
  fill_to_bson(code, cfg)
  fill_to_document(code, cfg)
  fill_load(code, cfg)
  fill_append_updates(code, cfg)
  fill_reset_children(code, cfg)
  fill_to_sub_update(code, cfg)
  fill_to_delete(code, cfg)
  fill_deleted_size(code, cfg)
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
    unless field.has_key? 'bname'
      field['bname'] = field['name']
    end
  end
end.each do |model|
  unless model.has_key? 'package'
    model['package'] = cfg['package']
  end
  if parents.has_key? model['name']
    model['parent'] = parents[model['name']]
    model['bname'] = bnames[model['name']]
  end
end.each do |model|
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
  require 'fileutils'
  package_dir = File.join(ARGV[1], File.join(*model['package'].split('.')))
  unless File.directory?(package_dir)
    FileUtils.mkdir_p(package_dir)
  end
  filename = "#{model['name']}.java"
  puts "Generating #{filename} ... (on path: #{package_dir})"
  File.open(File.join(package_dir, filename), "w") do |io|
    io.syswrite(code)
  end
  puts "OK"
end

puts "Done."