package io.adagate.handlers.database.epochs;

import io.adagate.handlers.database.AbstractDatabaseHandler;
import io.vertx.core.eventbus.Message;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.templates.SqlTemplate;

import static io.adagate.utils.ExceptionHandler.handleError;
import static io.vertx.core.Future.succeededFuture;
import static java.util.Collections.emptyMap;

public final class GetLatestEpochNumber extends AbstractDatabaseHandler<Message<Object>> {

    public static final String ADDRESS = "io.adagate.epochs.get.latest.number";
    private static final String QUERY = "SELECT MAX(epoch.no) FROM epoch";

    public GetLatestEpochNumber(PgPool pool) { super(pool); }

    @Override
    protected String query() {
        return QUERY;
    }

    @Override
    public void handle(Message<Object> message) {
        SqlTemplate
            .forQuery(client, query())
            .execute(emptyMap())
            .compose(this::mapToFirstJsonResult)
            .compose(result -> succeededFuture(result.getInteger("max")))
            .onSuccess(message::reply);
    }
}
