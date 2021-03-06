package denominator.dynect;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterators.filter;
import static com.google.common.collect.Ordering.usingToString;

import java.util.Iterator;

import javax.inject.Inject;

import org.jclouds.dynect.v3.DynECTApi;
import org.jclouds.dynect.v3.domain.RecordId;
import org.jclouds.dynect.v3.features.RecordApi;

import denominator.ResourceRecordSetApi;
import denominator.model.ResourceRecordSet;

public final class DynECTResourceRecordSetApi implements denominator.ResourceRecordSetApi {
    static final class Factory implements denominator.ResourceRecordSetApi.Factory {

        private final DynECTApi api;

        @Inject
        Factory(DynECTApi api) {
            this.api = api;
        }

        @Override
        public ResourceRecordSetApi create(final String zoneName) {
            return new DynECTResourceRecordSetApi(api.getRecordApiForZone(zoneName));
        }
    }

    private final RecordApi api;

    DynECTResourceRecordSetApi(RecordApi api) {
        this.api = api;
    }

    @Override
    public Iterator<ResourceRecordSet<?>> list() {
        Iterator<RecordId> orderedKeys = api.list().toSortedList(usingToString()).iterator();
        return filter(new GroupByRecordNameAndTypeIterator(api, orderedKeys), notNull());
    }
}
