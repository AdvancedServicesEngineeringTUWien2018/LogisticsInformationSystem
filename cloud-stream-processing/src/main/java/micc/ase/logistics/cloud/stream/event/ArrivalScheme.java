package micc.ase.logistics.cloud.stream.event;

//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.serialization.TypeInformationSerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeSerializer;

public class ArrivalScheme extends TypeInformationSerializationSchema<ArrivalDTO> {
    //JsonNodeDeserializationSchema { //JsonDeserializationSchema {// DeserializationSchema<T>, SerializationSchema<T> {

    public ArrivalScheme(TypeSerializer<ArrivalDTO> serializer) {
        super(TypeInformation.of(ArrivalDTO.class), serializer);
    }

//    @Override
//    public ArrivalDTO deserialize(byte[] message) {
//        return super.deserialize(message);
//    }


//    private final Class<T> clazz;
//    private ObjectMapper mapper;
//
//    public ArrivalScheme(Class<T> clazz) {
//        this.clazz = clazz;
//    }
//
//    /**
//     * Serializes the incoming element to a specified type.
//     *
//     * @param element The incoming element to be serialized
//     * @return The serialized element.
//     */
//    @Override
//    public byte[] serialize(T element) {
//        if (this.mapper == null) {
//            this.mapper = new ObjectMapper();
//        }
//
//        try {
//            return this.mapper.writeValueAsBytes(element);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     * Deserializes the byte message.
//     *
//     * @param message The message, as a byte array.
//     * @return The deserialized message as an object (null if the message cannot be deserialized).
//     */
//    @Override
//    public T deserialize(byte[] message) throws IOException {
//        if (this.mapper == null) {
//            this.mapper = new ObjectMapper();
//        }
//
//        return this.mapper.readValue(message, clazz);
//    }
//
//    /**
//     * Method to decide whether the element signals the end of the stream. If
//     * true is returned the element won't be emitted.
//     *
//     * @param nextElement The element to test for the end-of-stream signal.
//     * @return True, if the element signals end of stream, false otherwise.
//     */
//    @Override
//    public boolean isEndOfStream(T nextElement) {
//        return false;
//    }
//
//
//    /**
//     * Gets the data type (as a {@link TypeInformation}) produced by this function or input format.
//     *
//     * @return The data type produced by this function or input format.
//     */
//    @Override
//    public TypeInformation<T> getProducedType() {
//        return TypeExtractor.getForClass(clazz);
//    }
}
