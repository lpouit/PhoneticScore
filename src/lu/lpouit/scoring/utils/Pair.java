package lu.lpouit.scoring.utils;

public class Pair<K, V> {

	protected K key;
	protected V value;
	
	public Pair(K k, V v) {
		this.key = k;
		this.value = v;
	}
	
	public K getKey() {
		return key;
	}
	public void setKey(K key) {
		this.key = key;
	}
	public V getValue() {
		return value;
	}
	public void setValue(V value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return key+"="+value;
	}
	
}
