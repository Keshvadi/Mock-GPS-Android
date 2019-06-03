package ir.babak.mockgps.Database.Entitiy;

import android.os.Parcel;
import android.os.Parcelable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class PositionsEntitiy implements Parcelable {
    @Id
    public long id;
    public double lat;
    public double lng;

    public PositionsEntitiy(){

    }

    public PositionsEntitiy(long id,double lat,double lng){
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }

    protected PositionsEntitiy(Parcel in) {
        id = in.readLong();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PositionsEntitiy> CREATOR = new Creator<PositionsEntitiy>() {
        @Override
        public PositionsEntitiy createFromParcel(Parcel in) {
            return new PositionsEntitiy(in);
        }

        @Override
        public PositionsEntitiy[] newArray(int size) {
            return new PositionsEntitiy[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Position{" +
                "id=" + id +
                ", lat='" + lat + '\'' +
                ", lng=" + lng +
                '}';
    }



}
