package de.we2.am.therealone.mapper;

public interface ObjectMapper<TO, DO> {

    DO convertTOtoDO(TO transferObject);

    TO convertDOtoTO(DO dataObject);
}
