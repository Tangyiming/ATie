package application;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class TableCheckedValueFactory
		implements Callback<TableColumn.CellDataFeatures<TableModel, CheckBox>, ObservableValue<CheckBox>> {
	@Override
	public ObservableValue<CheckBox> call(TableColumn.CellDataFeatures<TableModel, CheckBox> param) {
		TableModel tm = param.getValue();
		CheckBox checkBox = new CheckBox();
		checkBox.selectedProperty().setValue(tm.isChecked());
		checkBox.selectedProperty().addListener((ov, old_val, new_val) -> {
			tm.setChecked(new_val);
		});
		return new SimpleObjectProperty<>(checkBox);
	}
}
