package local.expenses;
import androidx.room.*;
import androidx.annotation.NonNull;
@Entity(tableName="expenses",foreignKeys=@ForeignKey(entity=Category.class,parentColumns="name",childColumns="category",onDelete=ForeignKey.RESTRICT),indices={@Index("category"),@Index("time")})
public class Expense {
 @PrimaryKey(autoGenerate=true) public long id;
 public long cents;
 @NonNull public String category="";
 public long time;
 @NonNull public String note="";
}
