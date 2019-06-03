package ir.babak.mockgps.Database.Entitiy;

import android.os.Parcel;
import android.os.Parcelable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class PeriodEntitiy {
    @Id
    public long id;
    public long Period;

    public PeriodEntitiy(){

    }

    public PeriodEntitiy(long id, long Period){
        this.id = id;
        this.Period = Period;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPeriod() {
        return Period;
    }

    public void setPeriod(long period) {
        Period = period;
    }
}
