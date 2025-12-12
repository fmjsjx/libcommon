package com.github.fmjsjx.libcommon.bson;

import org.bson.BsonString;

/**
 * Constants for MongoDB.
 *
 * @author MJ Fang
 * @since 3.2
 */
public class Constants {

    /**
     * Meta data keywords.
     */
    public static final class MetaDataKeywords {
        /**
         * {@code "testScore"}
         */
        public static final String TEXT_SCORE = MetaDataKeyword.TEXT_SCORE.value();
        /**
         * {@code "indexKey"}
         */
        public static final String INDEX_KEY = MetaDataKeyword.INDEX_KEY.value();

        private MetaDataKeywords() {
        }
    }

    /**
     * Meta data keywords as {@link BsonString}s.
     */
    public static final class BsonMetaDataKeywords {
        /**
         * {@code "testScore"}
         */
        public static final BsonString TEXT_SCORE = MetaDataKeyword.TEXT_SCORE.toBsonString();
        /**
         * {@code "indexKey"}
         */
        public static final BsonString INDEX_KEY = MetaDataKeyword.INDEX_KEY.toBsonString();

        private BsonMetaDataKeywords() {
        }
    }

    /**
     * BSON date units.
     */
    public static final class DateUnits {
        /**
         * year
         */
        public static final String YEAR = DateUnit.YEAR.value();
        /**
         * quarter
         */
        public static final String QUARTER = DateUnit.QUARTER.value();
        /**
         * week
         */
        public static final String WEEK = DateUnit.WEEK.value();
        /**
         * month
         */
        public static final String MONTH = DateUnit.MONTH.value();
        /**
         * day
         */
        public static final String DAY = DateUnit.DAY.value();
        /**
         * hour
         */
        public static final String HOUR = DateUnit.HOUR.value();
        /**
         * minute
         */
        public static final String MINUTE = DateUnit.MINUTE.value();
        /**
         * second
         */
        public static final String SECOND = DateUnit.SECOND.value();
        /**
         * millisecond
         */
        public static final String MILLISECOND = DateUnit.MILLISECOND.value();

        private DateUnits() {
        }
    }

    /**
     * BSON date units as {@link BsonString}s.
     */
    public static final class BsonDateUnits {
        /**
         * year
         */
        public static final BsonString YEAR = DateUnit.YEAR.toBsonString();
        /**
         * quarter
         */
        public static final BsonString QUARTER = DateUnit.QUARTER.toBsonString();
        /**
         * week
         */
        public static final BsonString WEEK = DateUnit.WEEK.toBsonString();
        /**
         * month
         */
        public static final BsonString MONTH = DateUnit.MONTH.toBsonString();
        /**
         * day
         */
        public static final BsonString DAY = DateUnit.DAY.toBsonString();
        /**
         * hour
         */
        public static final BsonString HOUR = DateUnit.HOUR.toBsonString();
        /**
         * minute
         */
        public static final BsonString MINUTE = DateUnit.MINUTE.toBsonString();
        /**
         * second
         */
        public static final BsonString SECOND = DateUnit.SECOND.toBsonString();
        /**
         * millisecond
         */
        public static final BsonString MILLISECOND = DateUnit.MILLISECOND.toBsonString();

        private BsonDateUnits() {
        }
    }

    /**
     * Day of weeks.
     */
    public static final class DayOfWeeks {

        /**
         * SUNDAY => "sun"
         */
        public static final String SUNDAY = "sun";
        /**
         * MONDAY => "mon"
         */
        public static final String MONDAY = "mon";
        /**
         * MONDAY => "mon
         */
        public static final String TUESDAY = "tue";
        /**
         * WEDNESDAY => "wed"
         */
        public static final String WEDNESDAY = "wed";
        /**
         * THURSDAY => "thu"
         */
        public static final String THURSDAY = "thu";
        /**
         * FRIDAY => "fri"
         */
        public static final String FRIDAY = "fri";
        /**
         * SATURDAY => "sat"
         */
        public static final String SATURDAY = "sat";

        private DayOfWeeks() {
        }
    }

    /**
     * Day of weeks as {@link BsonString}s.
     */
    public static final class BsonDayOfWeeks {

        /**
         * SUNDAY => "sun"
         */
        public static final BsonString SUNDAY = new BsonString(DayOfWeeks.SUNDAY);
        /**
         * MONDAY => "mon"
         */
        public static final BsonString MONDAY = new BsonString(DayOfWeeks.MONDAY);
        /**
         * MONDAY => "mon
         */
        public static final BsonString TUESDAY = new BsonString(DayOfWeeks.TUESDAY);
        /**
         * WEDNESDAY => "wed"
         */
        public static final BsonString WEDNESDAY = new BsonString(DayOfWeeks.WEDNESDAY);
        /**
         * THURSDAY => "thu"
         */
        public static final BsonString THURSDAY = new BsonString(DayOfWeeks.THURSDAY);
        /**
         * FRIDAY => "fri"
         */
        public static final BsonString FRIDAY = new BsonString(DayOfWeeks.FRIDAY);
        /**
         * SATURDAY => "sat"
         */
        public static final BsonString SATURDAY = new BsonString(DayOfWeeks.SATURDAY);

        private BsonDayOfWeeks() {
        }
    }

    /**
     * System variables.
     */
    public static final class SystemVariables {
        /**
         * $$NOW
         */
        public static final String NOW = "$$NOW";
        /**
         * $$CLUSTER_TIME
         */
        public static final String CLUSTER_TIME = "$$CLUSTER_TIME";
        /**
         * $$ROOT
         */
        public static final String ROOT = "$$ROOT";
        /**
         * $$CURRENT
         */
        public static final String CURRENT = "$$CURRENT";
        /**
         * $$REMOVE
         */
        public static final String REMOVE = "$$REMOVE";
        /**
         * $$DESCEND
         */
        public static final String DESCEND = "$$DESCEND";
        /**
         * $$PRUNE
         */
        public static final String PRUNE = "$$PRUNE";
        /**
         * $$KEEP
         */
        public static final String KEEP = "$$KEEP";
        /**
         * $$SEARCH_META
         */
        public static final String SEARCH_META = "$$SEARCH_META";

        private SystemVariables() {
        }
    }

    /**
     * System variables as {@link BsonString}s.
     */
    public static final class BsonSystemVariables {
        /**
         * $$NOW
         */
        public static final BsonString NOW = new BsonString("$$NOW");
        /**
         * $$CLUSTER_TIME
         */
        public static final BsonString CLUSTER_TIME = new BsonString("$$CLUSTER_TIME");
        /**
         * $$ROOT
         */
        public static final BsonString ROOT = new BsonString("$$ROOT");
        /**
         * $$CURRENT
         */
        public static final BsonString CURRENT = new BsonString("$$CURRENT");
        /**
         * $$REMOVE
         */
        public static final BsonString REMOVE = new BsonString("$$REMOVE");
        /**
         * $$DESCEND
         */
        public static final BsonString DESCEND = new BsonString("$$DESCEND");
        /**
         * $$PRUNE
         */
        public static final BsonString PRUNE = new BsonString("$$PRUNE");
        /**
         * $$KEEP
         */
        public static final BsonString KEEP = new BsonString("$$KEEP");
        /**
         * $$SEARCH_META
         */
        public static final BsonString SEARCH_META = new BsonString("$$SEARCH_META");

        private BsonSystemVariables() {
        }
    }

    private Constants() {
    }

}
