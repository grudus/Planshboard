package com.grudus.planshboard.utils.jooq

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.jooq.*
import org.jooq.impl.DSL
import java.sql.SQLException
import java.sql.SQLFeatureNotSupportedException
import java.sql.Types
import java.util.*


// Copied from https://www.jooq.org/doc/3.10/manual/code-generation/custom-data-type-bindings/

@Suppress("unused") // used in jooq code generation
class StringBinding : Binding<Any, JsonNode> {
    private val objectMapper = ObjectMapper()

    override fun converter(): Converter<Any, JsonNode?> {
        return Converter.of(Any::class.java, JsonNode::class.java,
                { if (it == null) null else objectMapper.readTree(it.toString())  },
                { it?.toString() })
    }

    @Throws(SQLException::class)
    override fun sql(ctx: BindingSQLContext<JsonNode?>) {
        ctx.render().visit(DSL.`val`(ctx.convert(converter()).value())).sql("::json")
    }

    @Throws(SQLException::class)
    override fun register(ctx: BindingRegisterContext<JsonNode?>) {
        ctx.statement()
                .registerOutParameter(ctx.index(),
                        Types.VARCHAR)
    }

    @Throws(SQLException::class)
    override fun set(ctx: BindingSetStatementContext<JsonNode?>) {
        ctx.statement()
                .setString(ctx.index(),
                        Objects.toString(ctx.convert(converter()).value(),
                                null))
    }

    @Throws(SQLException::class)
    override fun set(ctx: BindingSetSQLOutputContext<JsonNode?>?) {
        throw SQLFeatureNotSupportedException()
    }

    @Throws(SQLException::class)
    override fun get(ctx: BindingGetResultSetContext<JsonNode?>) {
        ctx.convert(converter()).value(ctx.resultSet().getString(ctx.index()))
    }

    @Throws(SQLException::class)
    override fun get(ctx: BindingGetStatementContext<JsonNode?>) {
        ctx.convert(converter()).value(ctx.statement().getString(ctx.index()))
    }

    @Throws(SQLException::class)
    override fun get(ctx: BindingGetSQLInputContext<JsonNode?>?) {
        throw SQLFeatureNotSupportedException()
    }
}
