package com.diagnostics;

import com.datastax.oss.driver.api.core.AllNodesFailedException;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import com.datastax.oss.driver.api.core.metadata.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * This Diagnostics class is meant to be a simple example of doing basic DDL and CRUD operations against an
 * Apache Cassandra database cluster while providing a window into how the operations are performed within
 * the cluster.
 */
public class QueryDiagnostics {
    private static final Logger logger = LoggerFactory.getLogger(QueryDiagnostics.class);

    private static final String KEYSPACE_DEFINITION = "CREATE KEYSPACE IF NOT EXISTS foo WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1};";
    private static final String TABLE_DEFINITION    = "CREATE TABLE IF NOT EXISTS foo.bar (id int PRIMARY KEY, value int);";
    private static final String INSERT_STATEMENT    = "INSERT INTO foo.bar (id, value) values (?, ?);";
    private static final String SELECT_STATEMENT    = "select * from foo.bar where id = ?";
    private static final int    NUM_ROWS            = 10;

    public static void main (String [] args) throws Exception {
        CqlSession session = null;
        try {
            session = CqlSession.builder().build();
            setupSchema(session);
            writeData(session);
            readData(session);
        } catch (AllNodesFailedException allNodesFailedException) {
            logger.error(allNodesFailedException.getMessage(), allNodesFailedException);
        } finally {
            if (session != null)
                session.close();
        }
    }

    /**
     * Creates the keyspace and table schema safely, waiting for schema agreement
     * @param session the Cassandra database session
     * @throws Exception Any exception will be passed up to the caller
     */
    private static void setupSchema(CqlSession session) throws Exception {
        final Statement createKeyspace = SimpleStatement.builder(KEYSPACE_DEFINITION).setTracing().build();
        final Statement createTable = SimpleStatement.builder(TABLE_DEFINITION).setTracing().build();

        logger.info("Creating keyspace foo");
        ExecutionInfo executionInfo = execute(session, createKeyspace);
        if (!executionInfo.isSchemaInAgreement()) {
            throw new Exception("Cannot reach schema agreement");
        }
        logger.info("Created keyspace foo");

        logger.info("Creating table bar");
        executionInfo = execute(session, createTable);
        if (!executionInfo.isSchemaInAgreement()) {
            throw new Exception("Cannot reach schema agreement");
        }
        logger.info("Created table bar");
    }

    /**
     * Writes some data to the table
     * @param session the Cassandra database session
     */
    private static void writeData(CqlSession session) {
        final PreparedStatement prepared = session.prepare(INSERT_STATEMENT);
        BoundStatement bound = null;
        for (int i=0; i<NUM_ROWS; i++) {
            logger.info("Writing with id " + i);
            bound = prepared.bind(i, i).setTracing(true);
            execute(session, bound);
        }
    }

    /**
     * Reads back data from the table
     * @param session the Cassandra database session
     */
    private static void readData(CqlSession session) {
        final PreparedStatement prepared = session.prepare(SELECT_STATEMENT);
        BoundStatement bound = null;
        for (int i=0; i<NUM_ROWS; i++) {
            logger.info("Reading with id " + i);
            bound = prepared.bind(i).setTracing(true);
            execute(session, bound);
        }
    }

    /**
     * Execute the given Statement and if tracing is enabled, print out tracing metadata
     * @param session the Cassandra database session
     * @param statement the query to execute
     * @return The ExecutionInfo in case the caller needs it
     */
    private static ExecutionInfo execute(CqlSession session, Statement statement) {
        ResultSet rs = session.execute(statement);
        ExecutionInfo executionInfo = rs.getExecutionInfo();

        // If tracing is enabled for the query, print all of the tracing information
        if (executionInfo.getTracingId() != null) {
            QueryTrace trace = executionInfo.getQueryTrace();
            StringBuilder builder = new StringBuilder("Query Trace:").append(System.lineSeparator());
            builder.append(String.format(
                    "'%s' to %s took %dμs%n",
                    trace.getRequestType(), trace.getCoordinator(), trace.getDurationMicros()));
            builder.append(String.format("%10s | %20s | %s%n", "Elapsed", "Source", "Activity"));
            for (TraceEvent event : trace.getEvents()) {
                builder.append(String.format(
                        "%10d | %20s | %s%n",
                        event.getSourceElapsedMicros(), event.getSource(), event.getActivity()));
            }
            logger.info(builder.toString());
        }
        return executionInfo;
    }
}
