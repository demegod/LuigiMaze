public class Location {
	public int h;
	public int w;
	
	public Location(int h, int w) {
		this.h = h;
		this.w = w;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Location)) return false;
		Location that = (Location)(obj);
		return this.h == that.h && this.w == that.w;
	}
}
