package dev.scaraz.mars.common.domain.dynamic;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public class DynamicSqlType implements UserType<DynamicType> {

    @Override
    public int getSqlType() {
        return Types.VARCHAR;
    }

    @Override
    public Class<DynamicType> returnedClass() {
        return DynamicType.class;
    }

    @Override
    public boolean equals(DynamicType x, DynamicType y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(DynamicType x) {
        return Objects.hashCode(x);
    }

    @Override
    public DynamicType nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String string = rs.getString(position);
        if (string == null) return null;
        return DynamicType.from(string);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, DynamicType value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (Objects.isNull(value)) st.setNull(index, getSqlType());
        else st.setString(index, value.toString());
    }

    @Override
    public DynamicType deepCopy(DynamicType value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(DynamicType value) {
        return value.toString();
    }

    @Override
    public DynamicType assemble(Serializable cached, Object owner) {
        return DynamicType.from((String) cached);
    }

}
