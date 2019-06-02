package ir.babak.mockgps.Database;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class PositionsEntitiy {
    @Id public long id;
    public long lat;
    public long lng;
}
