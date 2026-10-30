package local.expenses;
import androidx.room.*;
import androidx.annotation.NonNull;
@Entity(tableName="settings")
public class Setting {
 @PrimaryKey @NonNull public String key="";
 @NonNull public String value="";
 public Setting(@NonNull String key,@NonNull String value){this.key=key;this.value=value;}
}
