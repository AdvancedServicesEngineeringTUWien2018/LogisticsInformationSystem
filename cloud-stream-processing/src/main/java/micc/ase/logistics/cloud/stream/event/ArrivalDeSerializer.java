package micc.ase.logistics.cloud.stream.event;


import org.apache.flink.api.common.typeutils.CompatibilityResult;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.TypeSerializerConfigSnapshot;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataOutputView;

import java.io.IOException;

public class ArrivalDeSerializer extends TypeSerializer<ArrivalDTO> {

    @Override
    public boolean isImmutableType() {
        return false;
    }

    @Override
    public TypeSerializer<ArrivalDTO> duplicate() {
        return this;
    }

    @Override
    public ArrivalDTO createInstance() {
        return new ArrivalDTO();
    }

    @Override
    public ArrivalDTO copy(ArrivalDTO from) {
        return new ArrivalDTO(from.getVehicleId(), from.getLocationId(), from.getLocation(), from.getTimestamp());
    }

    @Override
    public ArrivalDTO copy(ArrivalDTO from, ArrivalDTO reuse) {
        reuse.setLocation(from.getLocation());
        reuse.setLocationId(from.getLocationId());
        reuse.setTimestamp(from.getTimestamp());
        reuse.setVehicleId(from.getVehicleId());
        return reuse;
    }

    @Override
    public int getLength() {
        return -1;
    }

    @Override
    public void serialize(ArrivalDTO record, DataOutputView target) throws IOException {



//        target.write(record.);
    }

    @Override
    public ArrivalDTO deserialize(DataInputView source) throws IOException {
        return null;
    }

    @Override
    public ArrivalDTO deserialize(ArrivalDTO reuse, DataInputView source) throws IOException {
        return null;
    }

    @Override
    public void copy(DataInputView source, DataOutputView target) throws IOException {

    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public boolean canEqual(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public TypeSerializerConfigSnapshot snapshotConfiguration() {
        return null;
    }

    @Override
    public CompatibilityResult<ArrivalDTO> ensureCompatibility(TypeSerializerConfigSnapshot configSnapshot) {
        return null;
    }
}
