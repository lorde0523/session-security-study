package com.example.sessionsecurity.common.multiDb2;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class MultiDb2RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return MultiDb2DataSourceContext.get();
    }
}
