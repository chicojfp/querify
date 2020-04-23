package io.breezil.queryfier.serializer;

public interface ISerializer {
	
	default public <T> String serialize(Class<T> clazz, String value) {
		if (value != null) {
			return value.toString();
		}
		return null;
	}

	public default <T> T deserialize(Class<T> clazz, Object patch) {
		if (patch.getClass().isAssignableFrom(clazz)) {
			return (T) patch;
		}
		return null;
	}

}
