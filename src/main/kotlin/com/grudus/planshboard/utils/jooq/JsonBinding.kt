package com.grudus.planshboard.utils.jooq

import org.jooq.*
import org.jooq.conf.ParamType
import org.jooq.impl.DSL
import java.sql.SQLException
import java.sql.SQLFeatureNotSupportedException
import java.sql.Types

// Copied from https://www.jooq.org/doc/3.10/manual/code-generation/custom-data-type-bindings/

@Suppress("unused") // used in jooq code generation
class StringBinding : Binding<Any, String> {
    override fun converter(): Converter<Any, String> {
        return Converter.of(Any::class.java, String::class.java,
                { it?.toString() },
                { it })
    }

    override fun sql(ctx: BindingSQLContext<String>) {
        if (ctx.render().paramType() === ParamType.INLINED)
            ctx.render().visit(DSL.inline(ctx.convert(converter()).value())).sql("::json")
        else
            ctx.render().sql("?::json")
    }

    override fun register(ctx: BindingRegisterContext<String>) {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR)
    }

    override fun set(ctx: BindingSetStatementContext<String>) {
            ctx.statement().setString(ctx.index(), ctx.convert(converter()).value()?.toString());
    }

    // Converting the String to a String value and setting that on a JDBC PreparedStatement
    override fun get(ctx: BindingGetResultSetContext<String>): Unit {
        ctx.convert(converter()).value(ctx.resultSet().getString(ctx.index()))
    }

    override fun get(ctx: BindingGetStatementContext<String>) {
        ctx.convert(converter()).value(ctx.statement().getString(ctx.index()))
    }

    // Setting a value on a JDBC SQLOutput (useful for Oracle Any types)
    @Throws(SQLException::class)
    override fun set(ctx: BindingSetSQLOutputContext<String>?): Unit {
        throw SQLFeatureNotSupportedException()
    }

    // Getting a value from a JDBC SQLInput (useful for Oracle OBJECT types)
    @Throws(SQLException::class)
    override fun get(ctx: BindingGetSQLInputContext<String>?) {
        throw SQLFeatureNotSupportedException()
    }

}
