package com.heihei.model.network;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.net.Uri;

public abstract class Model {
	public static <T extends Model> T parseFromJSON(Class<T> cls, String json) {
		if (cls == null || json == null) {
			T result = null;
			if (cls != null) {
				try {
					result = cls.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return result;
		}

		try {
			JSONObject jsonObject = new JSONObject(json);
			return parseFromJSONObject(cls, jsonObject);
		} catch (Exception e) {
			// if (Log.IsDeveloperMode)
			// Log.v(e, "; (", json, ")");
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Model> T parseFromJSONObject(Class<T> cls, JSONObject jsonObject) {
		T result = null;
		try {
			result = cls.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}

		if (jsonObject == null)
			return result;

		Field[] fields = cls.getFields();
		for (Field field : fields) {
			int modifier = field.getModifiers();
			if (!Modifier.isPublic(modifier) || Modifier.isStatic(modifier) || Modifier.isTransient(modifier)) {
				continue;
			}

			String fieldName = field.getName();
			Class<?> fieldType = field.getType();

			try {
				if (fieldType.equals(Long.class) || fieldType.equals(Long.TYPE)) {
					field.set(result, jsonObject.getLong(fieldName));
				} else if (fieldType.equals(Integer.class) || fieldType.equals(Integer.TYPE)) {
					field.set(result, jsonObject.getInt(fieldName));
				} else if (fieldType.equals(Boolean.class) || fieldType.equals(Boolean.TYPE)) {
					field.set(result, jsonObject.getBoolean(fieldName));
				} else if (fieldType.equals(Float.class) || fieldType.equals(Float.TYPE)) {
					field.set(result, (float) jsonObject.getDouble(fieldName));
				} else if (fieldType.equals(Double.class) || fieldType.equals(Double.TYPE)) {
					field.set(result, jsonObject.getDouble(fieldName));
				} else if (fieldType.equals(Date.class)) {
					field.set(result, new Date(jsonObject.getLong(fieldName)));
				} else if (fieldType.equals(String.class)) {
					field.set(result, jsonObject.isNull(fieldName) ? null : jsonObject.getString(fieldName));
				} else if (fieldType.equals(Uri.class)) {
					field.set(result, jsonObject.isNull(fieldName) ? null : Uri.parse(jsonObject.getString(fieldName)));
				} else if (Model.class.isAssignableFrom(fieldType)) {
					field.set(result, parseFromJSONObject((Class<? extends Model>) fieldType, jsonObject.getJSONObject(fieldName)));
				} else if (fieldType.equals(ArrayList.class)) {
					ParameterizedType genericType = (ParameterizedType) field.getGenericType();
					Type[] typeArguments = genericType.getActualTypeArguments();
					field.set(result, parseFromJSONArray((Class<?>) typeArguments[0], jsonObject.getJSONArray(fieldName)));
				} else if (fieldType.equals(JSONMap.class)) {
					ParameterizedType genericType = (ParameterizedType) field.getGenericType();
					Type[] typeArguments = genericType.getActualTypeArguments();
					field.set(result, parseFromJSONMap((Class<?>) typeArguments[0], jsonObject.getJSONObject(fieldName)));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public static <T> ArrayList<T> parseFromJSONArray(Class<T> cls, String json) {
		if (cls == null || json == null) {
			return new ArrayList<>();
		}

		try {
			JSONArray jsonArray = new JSONArray(json);
			return parseFromJSONArray(cls, jsonArray);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public static <T> JSONMap<T> parseFromJSONMap(Class<T> cls, JSONObject jsonObject) {
		JSONMap<T> result = new JSONMap<>();

		if (jsonObject == null || cls == null)
			return result;

		Iterator<?> iter = jsonObject.keys();
		while (iter != null && iter.hasNext()) {
			String key = (String) iter.next();
			T value = null;

			try {
				if (cls.equals(Long.class) || cls.equals(Long.TYPE)) {
					value = cls.cast(jsonObject.getLong(key));
				} else if (cls.equals(Integer.class) || cls.equals(Integer.TYPE)) {
					value = cls.cast(jsonObject.getInt(key));
				} else if (cls.equals(Boolean.class) || cls.equals(Boolean.TYPE)) {
					value = cls.cast(jsonObject.getBoolean(key));
				} else if (cls.equals(Float.class) || cls.equals(Float.TYPE)) {
					value = cls.cast(jsonObject.getDouble(key));
				} else if (cls.equals(Double.class) || cls.equals(Double.TYPE)) {
					value = cls.cast(jsonObject.getDouble(key));
				} else if (cls.equals(Date.class)) {
					value = cls.cast(new Date(jsonObject.getLong(key)));
				} else if (cls.equals(String.class)) {
					value = cls.cast(jsonObject.isNull(key) ? null : jsonObject.getString(key));
				} else if (cls.equals(Uri.class)) {
					value = cls.cast(jsonObject.isNull(key) ? null : Uri.parse(jsonObject.getString(key)));
				} else if (Model.class.isAssignableFrom(cls)) {
					value = (T) parseFromJSONObject((Class<? extends Model>) cls, jsonObject.getJSONObject(key));
				} else {
					throw new Exception("Unsupported type: " + cls.getName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (value != null)
				result.put(key, value);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static <T> ArrayList<T> parseFromJSONArray(Class<T> cls, JSONArray jsonArray) {
		ArrayList<T> result = new ArrayList<>();

		if (jsonArray == null || cls == null)
			return result;

		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				if (cls.equals(Long.class) || cls.equals(Long.TYPE)) {
					result.add(cls.cast(jsonArray.getLong(i)));
				} else if (cls.equals(Integer.class) || cls.equals(Integer.TYPE)) {
					result.add(cls.cast(jsonArray.getInt(i)));
				} else if (cls.equals(Boolean.class) || cls.equals(Boolean.TYPE)) {
					result.add(cls.cast(jsonArray.getBoolean(i)));
				} else if (cls.equals(Float.class) || cls.equals(Float.TYPE)) {
					result.add(cls.cast(jsonArray.getDouble(i)));
				} else if (cls.equals(Double.class) || cls.equals(Double.TYPE)) {
					result.add(cls.cast(jsonArray.getDouble(i)));
				} else if (cls.equals(Date.class)) {
					result.add(cls.cast(jsonArray.getLong(i)));
				} else if (cls.equals(String.class)) {
					result.add(jsonArray.isNull(i) ? null : cls.cast(jsonArray.getString(i)));
				} else if (cls.equals(Uri.class)) {
					result.add(jsonArray.isNull(i) ? null : cls.cast(Uri.parse(jsonArray.getString(i))));
				} else if (Model.class.isAssignableFrom(cls)) {
					result.add((T) parseFromJSONObject((Class<? extends Model>) cls, jsonArray.getJSONObject(i)));
				} else {
					throw new Exception("Unsupported type: " + cls.getName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public static <T> String toJSON(Model t) {
		return toJSONObject(t).toString();
	}

	public static <T> JSONObject toJSONObject(Model t) {
		JSONObject jsonObj = new JSONObject();

		Class<?> cls = t.getClass();
		if (cls == null)
			return jsonObj;

		Field[] fields = cls.getFields();
		if (fields == null)
			return jsonObj;

		for (Field field : fields) {
			int modifier = field.getModifiers();
			if (!Modifier.isPublic(modifier) || Modifier.isStatic(modifier) || Modifier.isTransient(modifier)) {
				continue;
			}

			String fieldName = field.getName();
			Class<?> fieldType = field.getType();

			try {
				if (field.get(t) == null) {
					continue;
				}
				if (fieldType.equals(Long.class) || fieldType.equals(Long.TYPE)) {
					jsonObj.put(fieldName, field.get(t));
				} else if (fieldType.equals(Integer.class) || fieldType.equals(Integer.TYPE)) {
					jsonObj.put(fieldName, field.get(t));
				} else if (fieldType.equals(Boolean.class) || fieldType.equals(Boolean.TYPE)) {
					jsonObj.put(fieldName, field.get(t));
				} else if (fieldType.equals(Float.class) || fieldType.equals(Float.TYPE)) {
					jsonObj.put(fieldName, field.get(t));
				} else if (fieldType.equals(Double.class) || fieldType.equals(Double.TYPE)) {
					jsonObj.put(fieldName, field.get(t));
				} else if (fieldType.equals(Date.class)) {
					jsonObj.put(fieldName, Date.class.cast(field.get(t)).getTime());
				} else if (fieldType.equals(String.class)) {
					jsonObj.put(fieldName, String.class.cast(field.get(t)));
				} else if (fieldType.equals(Uri.class)) {
					jsonObj.put(fieldName, Uri.class.cast(field.get(t)).toString());
				} else if (Model.class.isAssignableFrom(fieldType)) {
					jsonObj.put(fieldName, toJSONObject((Model) field.get(t)));
				} else if (fieldType.equals(ArrayList.class)) {
					jsonObj.put(fieldName, toJSONArray((ArrayList<?>) field.get(t)));
				} else if (fieldType.equals(JSONMap.class)) {
					jsonObj.put(fieldName, toJSONObject((JSONMap<?>) field.get(t)));
				} else {
					throw new Exception("Unsupported type: " + fieldType.getName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return jsonObj;
	}

	public static <T> JSONObject toJSONObject(JSONMap<T> map) {
		JSONObject result = new JSONObject();

		for (Map.Entry<String, T> entry : map.entrySet()) {
			String key = entry.getKey();
			T value = entry.getValue();
			if (key == null || value == null)
				continue;

			Class<?> cls = value.getClass();

			try {
				if (cls.equals(Long.class) || cls.equals(Long.TYPE)) {
					result.put(key, value);
				} else if (cls.equals(Integer.class) || cls.equals(Integer.TYPE)) {
					result.put(key, value);
				} else if (cls.equals(Boolean.class) || cls.equals(Boolean.TYPE)) {
					result.put(key, value);
				} else if (cls.equals(Float.class) || cls.equals(Float.TYPE)) {
					result.put(key, value);
				} else if (cls.equals(Double.class) || cls.equals(Double.TYPE)) {
					result.put(key, value);
				} else if (cls.equals(Date.class)) {
					result.put(key, Date.class.cast(value).getTime());
				} else if (cls.equals(String.class)) {
					result.put(key, value);
				} else if (Uri.class.isAssignableFrom(cls)) {
					result.put(key, value);
				} else if (Model.class.isAssignableFrom(cls)) {
					result.put(key, toJSONObject((Model) value));
				} else {
					throw new Exception("Unsupported type: " + cls.getName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public static <T> JSONArray toJSONArray(ArrayList<T> array) {
		JSONArray jsonArray = new JSONArray();
		if (array == null || array.isEmpty())
			return jsonArray;

		Class<?> cls = array.get(0).getClass();

		for (T t : array) {
			try {
				if (cls.equals(Long.class) || cls.equals(Long.TYPE)) {
					jsonArray.put(t);
				} else if (cls.equals(Integer.class) || cls.equals(Integer.TYPE)) {
					jsonArray.put(t);
				} else if (cls.equals(Boolean.class) || cls.equals(Boolean.TYPE)) {
					jsonArray.put(t);
				} else if (cls.equals(Float.class) || cls.equals(Float.TYPE)) {
					jsonArray.put(t);
				} else if (cls.equals(Double.class) || cls.equals(Double.TYPE)) {
					jsonArray.put(t);
				} else if (cls.equals(Date.class)) {
					jsonArray.put(Date.class.cast(t).getTime());
				} else if (cls.equals(String.class)) {
					jsonArray.put(t);
				} else if (Uri.class.isAssignableFrom(cls)) {
					jsonArray.put(t);
				} else if (Model.class.isAssignableFrom(cls)) {
					jsonArray.put(toJSONObject((Model) t));
				} else {
					jsonArray.put(t);
					throw new Exception("Unsupported type: " + cls.getName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return jsonArray;
	}

	@Override
	public String toString() {
		return Model.toJSON(this);
	}

	public Long getKey() {
		return null;
	}

	public static class JSONMap<T> extends LinkedHashMap<String, T> {
		private static final long serialVersionUID = 4947403667374895879L;

		@Override
		public String toString() {
			return Model.toJSONObject(this).toString();
		}
	}
}
