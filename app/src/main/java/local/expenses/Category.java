package local.expenses;
import androidx.room.*;
import androidx.annotation.NonNull;
@Entity(tableName="categories")
public class Category {
 @PrimaryKey @NonNull public String name="";
 public Category(@NonNull String name){this.name=name;}
}
