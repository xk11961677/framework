/*
 * The MIT License (MIT)
 * Copyright © 2019-2020 <sky>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sky.framework.rule.engine.constant;


/**
 * 操作符
 *
 * @author
 */
public class OperatorConstants {

    private static OperatorConstants instance = new OperatorConstants();

    public static final String UNKNOWN = "$$$$$$NOT_EXISTS$$$$$$";

    public final static class OPR_CODE {

        public static final String EQUAL = "01";
        public static final String GREATER = "02";
        public static final String LESS = "03";
        public static final String NOT_EQUAL = "04";
        public static final String GREATER_EQUAL = "05";
        public static final String LESS_EQUAL = "06";
        public static final String INCLUDE = "07";
        public static final String NOT_INCLUDE = "08";
        public static final String IN = "09";
        public static final String NIN = "10";
        public static final String STRING_EQUAL = "11";
        public static final String NOT_STRING_EQUAL = "12";
        public static final String EQUAL_IGNORE_CASE = "13";
        public static final String NOT_EQUAL_IGNORE_CASE = "14";
        public static final String MATCH = "15";
        public static final String UNMATCH = "16";
        public static final String EXISTS = "17";
        public static final String SCHEMA = "18";
        public static final String NOT_EXISTS = "19";


        public static final String[] RESERVED_CODES = new String[]{EQUAL, GREATER, LESS, NOT_EQUAL, GREATER_EQUAL, LESS_EQUAL, INCLUDE, NOT_INCLUDE,
                IN, NIN, STRING_EQUAL, NOT_STRING_EQUAL, EQUAL_IGNORE_CASE, NOT_EQUAL_IGNORE_CASE, MATCH, UNMATCH, EXISTS, SCHEMA, NOT_EXISTS, "20"};

        public static final String[] RESERVED_VALUES = new String[]{"EQUAL", "GREATER", "LESS", "NOT_EQUAL", "GREATER_EQUAL", "LESS_EQUAL", "INCLUDE",
                "NOT_INCLUDE", "IN", "NIN", "STRING_EQUAL", "NOT_STRING_EQUAL", "EQUAL_IGNORE_CASE",
                "NOT_EQUAL_IGNORE_CASE", "MATCH", "UNMATCH", "EXISTS", "SCHEMA", "NOT_EXISTS", "20"};


        private static final String[] RESERVED_ALIAS_VALUES = new String[]{"EQ", "GT", "LT", "NEQ", "GTE", "LTE", "INCLUDE",
                "NOT_INCLUDE", "IN", "NIN", "STRING_EQUAL", "NOT_STRING_EQUAL", "EQ_IGNORE_CASE",
                "NEQ_IGNORE_CASE", "MATCH", "UNMATCH", "EXISTS", "SCHEMA", "NOT_EXISTS", "20"};


        public static String getCode(String value) {
            String temp = null;
            for (int iLoop = 0; iLoop < RESERVED_VALUES.length; iLoop++) {
                if (RESERVED_VALUES[iLoop].equalsIgnoreCase(value)) {
                    temp = RESERVED_CODES[iLoop];
                    break;
                }
            }

            if (temp == null) {
                for (int iLoop = 0; iLoop < RESERVED_ALIAS_VALUES.length; iLoop++) {
                    if (RESERVED_ALIAS_VALUES[iLoop].equalsIgnoreCase(value)) {
                        temp = RESERVED_CODES[iLoop];
                        break;
                    }
                }
            }
            return temp;
        }

        public static String getValue(String code) {
            String temp = null;
            for (int iLoop = 0; iLoop < RESERVED_CODES.length; iLoop++) {
                if (RESERVED_CODES[iLoop].equalsIgnoreCase(code)) {
                    temp = RESERVED_VALUES[iLoop];
                    break;
                }
            }
            return temp;
        }

        public static String getAliasValue(String code) {
            Integer temp = null;
            for (int iLoop = 0; iLoop < RESERVED_CODES.length; iLoop++) {
                if (RESERVED_CODES[iLoop].equalsIgnoreCase(code)) {
                    temp = iLoop;
                    break;
                }
            }
            if (temp != null) {
                return RESERVED_ALIAS_VALUES[temp];
            }
            return "";
        }
    }


    private OperatorConstants() {

    }

    public static OperatorConstants getInstance() {
        return instance;
    }

}
