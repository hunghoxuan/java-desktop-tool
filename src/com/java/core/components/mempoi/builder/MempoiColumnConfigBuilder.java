// package com.java.core.components.mempoi.builder;
//
// import com.java.core.components.mempoi.domain.MempoiColumnConfig;
// import
// java.core.components.mempoi.domain.datatransformation.DataTransformationFunction;
// import com.java.core.components.mempoi.exception.MempoiException;
// import com.java.core.components.mempoi.util.Errors;
// import org.apache.commons.lang3.StringUtils;
//
// public class MempoiColumnConfigBuilder {
// String columnName;
// DataTransformationFunction<Object, ?> dataTransformationFunction;
//
// /**
// * private constructor to lower constructor visibility from outside forcing
// the use of the static Builder pattern
// */
// private MempoiColumnConfigBuilder() {
// }
//
// /**
// * static method to create a new MempoiBuilder containing the received
// *
// * @return the current MempoiColumnConfigBuilder
// */
// public static MempoiColumnConfigBuilder aMempoiColumnConfig() {
// return new MempoiColumnConfigBuilder();
// }
//
// public MempoiColumnConfigBuilder withColumnName(String columnName) {
// this.columnName = columnName;
// return this;
// }
//
// public MempoiColumnConfigBuilder withDataTransformationFunction(
// DataTransformationFunction<Object, ?> dataTransformationFunction) {
// this.dataTransformationFunction = dataTransformationFunction;
// return this;
// }
//
//
// public MempoiColumnConfig build() {
//
// if (StringUtils.isEmpty(columnName)) {
// throw new MempoiException(Errors.ERR_MEMPOISHEET_LIST_NULL);
// }
//
// return new MempoiColumnConfig(columnName, dataTransformationFunction);
//
// }
// }
