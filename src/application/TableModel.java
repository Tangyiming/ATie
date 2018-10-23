package application;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TableModel {
	private Boolean Checked;
	private final IntegerProperty ID;
	private final SimpleStringProperty Casename;
	private final SimpleStringProperty ATCommand;
	private final SimpleStringProperty Expection;
	private final SimpleStringProperty Response;
	private final SimpleStringProperty Result;

	public TableModel(Boolean checked, Integer iD, String casename, String aTCommand, String expection, String response,
			String result) {
		super();
		this.ID = new SimpleIntegerProperty(iD);
		this.Checked = checked;
		this.Casename = new SimpleStringProperty(casename);
		this.ATCommand = new SimpleStringProperty(aTCommand);
		this.Expection = new SimpleStringProperty(expection);
		this.Response = new SimpleStringProperty(response);
		this.Result = new SimpleStringProperty(result);
	}

	public Boolean isChecked() {
		return this.Checked;
	}

	public void setChecked(Boolean checked) {
		this.Checked = checked;
	}

	public final SimpleStringProperty ATCommandProperty() {
		return this.ATCommand;
	}

	public final String getATCommand() {
		return this.ATCommandProperty().get();
	}

	public final void setATCommand(final String ATCommand) {
		this.ATCommandProperty().set(ATCommand);
	}

	public final SimpleStringProperty ExpectionProperty() {
		return this.Expection;
	}

	public final String getExpection() {
		return this.ExpectionProperty().get();
	}

	public final void setExpection(final String Expection) {
		this.ExpectionProperty().set(Expection);
	}

	public final SimpleStringProperty ResponseProperty() {
		return this.Response;
	}

	public final String getResponse() {
		return this.ResponseProperty().get();
	}

	public final void setResponse(final String Response) {
		this.ResponseProperty().set(Response);
	}

	public final SimpleStringProperty ResultProperty() {
		return this.Result;
	}

	public final String getResult() {
		return this.ResultProperty().get();
	}

	public final void setResult(final String Result) {
		this.ResultProperty().set(Result);
	}

	public final SimpleStringProperty CasenameProperty() {
		return this.Casename;
	}

	public final String getCasename() {
		return this.CasenameProperty().get();
	}

	public final void setCasename(final String Casename) {
		this.CasenameProperty().set(Casename);
	}

	public final IntegerProperty IDProperty() {
		return this.ID;
	}

	public final int getID() {
		return this.IDProperty().get();
	}

	public final void setID(final int ID) {
		this.IDProperty().set(ID);
	}
}