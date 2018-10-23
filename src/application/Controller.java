package application;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.concurrent.TimeUnit;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Controller implements SerialPortEventListener, Runnable {
	@FXML
	Button importCasesButton;
	@FXML
	Button sendATButton;
	@FXML
	Button openCoomButton;
	@FXML
	Button closeCoomButton;
	@FXML
	Button ClearTextAreaButton;
	@FXML
	Button ExportLogButton;
	@FXML
	Button runSelectButton;
	@FXML
	Button GenerateReportButton;
	@FXML
	Button OpenHelpButton;
	@FXML
	Button testAllButton;
	@FXML
	Label openStatus;
	@FXML
	Label openTestCases;
	@FXML
	ComboBox<String> Ports;
	@FXML
	ComboBox<String> BaudRateComboBox;
	@FXML
	ComboBox<String> DataBitsComboBox;
	@FXML
	ComboBox<String> ParityComboBox;
	@FXML
	ComboBox<String> StopBitsComboBox;
	@FXML
	TextArea ATResponse;
	@FXML
	TextField AT;
	@FXML
	TableView<TableModel> CasesTable;
	@FXML
	TableColumn<TableModel, CheckBox> tableCheckCoxList;
	@FXML
	TableColumn<TableModel, Integer> tableID;
	@FXML
	TableColumn<TableModel, String> tableATCMD;
	@FXML
	TableColumn<TableModel, String> tableExpection;
	@FXML
	TableColumn<TableModel, Label> tableResponse;
	@FXML
	TableColumn<TableModel, Label> tableResult;
	@FXML
	TableColumn<TableModel, String> caseName;
	@FXML
	Button pauseBtton;
	@FXML
	Button stopButton;
	@FXML
	ComboBox<String> timout;

	ObservableList<TableModel> lt = FXCollections.observableArrayList();
	static List<CommPortIdentifier> portList = new ArrayList<CommPortIdentifier>();
	byte[] readBuffer = new byte[10000];
	private static SerialPort serialPort;
	private static InputStream is;
	private static OutputStream os;
	private static boolean isOpen;
	private static Stage newAlertDialog;
	boolean check = false;
	int id;
	String casename = null;
	String at = null;
	String expe = null;
	String tempresp = "";
	String resp = "";
	String resu = null;
	String status = null;
	Thread th = null;

	public String CurrentTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Long now = System.currentTimeMillis();
		String dateString = df.format(now);
		return dateString;
	}

	public void sendAT(String ATCommd) throws InterruptedException {
		if (serialPort != null) {
			if (!ATCommd.equals("")) {
				try {
					serialPort.setDTR(false);
					os.write((ATCommd + "\r\n").getBytes());
					os.flush();
					ATResponse.appendText("[" + CurrentTime() + "]--> 发:" + ATCommd + "\n");
					// openStatus.setText("AT指令发送成功");
					TimeUnit.MILLISECONDS.sleep(200);
					ATResponse.appendText("[" + CurrentTime() + "]--> 收:" + resp + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				openStatus.setText("请输入AT指令");
			}
		}
	}

	@SuppressWarnings("rawtypes")
	@FXML
	public void chooseComm() {
		Ports.getItems().clear();
		Enumeration tempPortList;
		CommPortIdentifier portIp;
		tempPortList = CommPortIdentifier.getPortIdentifiers();
		List<String> pl = new ArrayList<String>();
		while (tempPortList.hasMoreElements()) {
			portIp = (CommPortIdentifier) tempPortList.nextElement();
			portList.add(portIp);
			String name = portIp.getName();
			String Owner = "";
			Owner = portIp.getCurrentOwner();
			if (Owner == null) {
				Owner = "";
			} else {
				Owner = "[" + Owner + "]";
			}
			pl.add(name + Owner);
		}
		Ports.getItems().addAll(pl);
	}

	@FXML
	public void openCoom() throws IOException {
		if (Ports.getValue() != null) {
			int len = portList.size();
			int i;
			for (i = 0; i < len; i++) {
				if (portList.get(i).getName().equals(Ports.getValue())) {
					break;
				}
			}
			try {
				serialPort = (SerialPort) portList.get(i).open("ATie", 10000);
				isOpen = true;
				openCoomButton.setDisable(isOpen);
				closeCoomButton.setDisable(!isOpen);
			} catch (PortInUseException e) {
				e.printStackTrace();
				openStatus.setText("该串口被其他应用占用");
			}
			try {
				is = serialPort.getInputStream();
				os = serialPort.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				serialPort.addEventListener(this);
				serialPort.notifyOnDataAvailable(true);
				serialPort.notifyOnOutputEmpty(true);
				serialPort.notifyOnCTS(true);
				serialPort.notifyOnDSR(true);
				serialPort.notifyOnCarrierDetect(true);
				serialPort.notifyOnOverrunError(true);
				serialPort.notifyOnParityError(true);
				serialPort.notifyOnFramingError(true);
				serialPort.notifyOnBreakInterrupt(true);
				serialPort.notifyOnParityError(true);
				serialPort.setRTS(true);
			} catch (TooManyListenersException e) {
				e.printStackTrace();
			}
			int BaudRate = Integer.parseInt(BaudRateComboBox.getValue());
			int DataBits = Integer.parseInt(DataBitsComboBox.getValue());
			int StopBits = 1;
			if (StopBitsComboBox.getValue().equals("1.5")) {
				StopBits = 3;
			} else if (StopBitsComboBox.getValue().equals("2")) {
				StopBits = 2;
			}
			int Parity = 0;
			if (ParityComboBox.getValue().equals("奇")) {
				Parity = 1;
			} else if (ParityComboBox.getValue().equals("偶")) {
				Parity = 2;
			} else if (ParityComboBox.getValue().equals("标志")) {
				Parity = 3;
			} else if (ParityComboBox.getValue().equals("空格")) {
				Parity = 4;
			}
			try {
				serialPort.setSerialPortParams(BaudRate, DataBits, StopBits, Parity);
				serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
				openStatus.setText("串口连接成功");
				Ports.setValue(Ports.getValue() + "[ATie]");
			} catch (UnsupportedCommOperationException e) {
				e.printStackTrace();
				openStatus.setText("串口连接失败");
			}
		}
	}

	@FXML
	public void closeCoom() {
		try {
			if (is != null) {
				is.close();
			}
			if (os != null) {
				os.close();
			}
			serialPort.notifyOnDataAvailable(false);
			serialPort.removeEventListener();
			if (serialPort != null) {
				serialPort.close();
			}
			isOpen = false;
			openCoomButton.setDisable(isOpen);
			closeCoomButton.setDisable(!isOpen);
			Ports.setValue(null);
			openStatus.setText("串口已成功关闭");
		} catch (IOException e) {
			openStatus.setText("串口关闭出错");
		}
	}

	@FXML
	public void sendAT() throws InterruptedException {
		String ATCommd = AT.getText();
		if (serialPort != null) {
			if (!ATCommd.equals("")) {
				try {
					serialPort.setDTR(false);
					os.write((ATCommd + "\r\n").getBytes());
					os.flush();
					ATResponse.appendText("[" + CurrentTime() + "]--> 发:" + ATCommd + "\n");
					openStatus.setText("AT指令发送成功");
					TimeUnit.MILLISECONDS.sleep(200);
					ATResponse.appendText("[" + CurrentTime() + "]--> 收:" + resp + "\n");
					resp = "";
				} catch (IOException e) {
				}
			} else {
				openStatus.setText("请输入AT指令");
			}
		} else {
			openStatus.setText("请连接串口");
		}
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.BI:/* Break interrupt,通讯中断 */
		case SerialPortEvent.OE:/* Overrun error，溢位错误 */
		case SerialPortEvent.FE:/* Framing error，传帧错误 */
		case SerialPortEvent.PE:/* Parity error，校验错误 */
		case SerialPortEvent.CD:/* Carrier detect，载波检测 */
		case SerialPortEvent.CTS:/* Clear to send，清除发送 */
		case SerialPortEvent.DSR:/* Data set ready，数据设备就绪 */
		case SerialPortEvent.RI:/* Ring indicator，响铃指示 */
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		case SerialPortEvent.DATA_AVAILABLE:
			try {
				int len = 0;
				while ((len = is.read(readBuffer)) != -1) {
					tempresp = new String(readBuffer, 0, len).trim() + "\n";
					resp = resp.concat(tempresp);
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	@FXML
	public void importCases() throws Exception {
		System.out.println("import");
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("导入测试用例");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Excel 工作薄", "*.xls", "*.xlsx"),
				new ExtensionFilter("所有文件", "*.*"));
		File selectedFile = fileChooser.showOpenDialog(newAlertDialog);
		String abpath = "";
		if (selectedFile != null) {
			abpath = selectedFile.getAbsolutePath();
		}
		openTestCases.setText(abpath);
		InputStream input = new FileInputStream(abpath);
		Workbook wb = null;
		if (abpath.endsWith(".xlsx")) {
			wb = new XSSFWorkbook(input);
		} else if (abpath.endsWith(".xls")) {
			wb = new HSSFWorkbook(input);
		} else {
			openStatus.setText("文件格式有误！请选择正确的文件！");
		}

		lt.removeAll(lt);
		Sheet sheet = wb.getSheetAt(0);
		Iterator<Row> rows = sheet.rowIterator();

		while (rows.hasNext()) {
			Row row = rows.next();
			id = row.getRowNum();
			for (int i = 0; i <= 3; i++) {
				switch (i) {
				case 0:
					if (row.getCell(i) != null) {
						casename = row.getCell(i).getStringCellValue();
					} else {
						casename = "";
					}
					break;
				case 1:
					if (row.getCell(i) != null) {
						at = row.getCell(i).getStringCellValue();
					} else {
						at = "";
					}
					break;
				case 2:
					if (row.getCell(i) != null) {
						expe = row.getCell(i).getStringCellValue();
					} else {
						expe = "";
					}
					break;
				case 3:
					if (row.getCell(i) != null) {
						resp = row.getCell(i).getStringCellValue();
					} else {
						resp = "";
					}
					break;
				}
			}
			lt.add(new TableModel(check, id, casename, at, expe, resp, resu));
		}
		tableID.setCellValueFactory(new PropertyValueFactory<TableModel, Integer>("iD"));
		caseName.setCellValueFactory(new PropertyValueFactory<TableModel, String>("casename"));
		tableATCMD.setCellValueFactory(new PropertyValueFactory<TableModel, String>("aTCommand"));
		tableExpection.setCellValueFactory(new PropertyValueFactory<TableModel, String>("expection"));
		tableResult.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<TableModel, Label>, ObservableValue<Label>>() {
					@Override
					public ObservableValue<Label> call(CellDataFeatures<TableModel, Label> param) {
						String val = "Empty";

						if (param != null && param.getValue() != null) {
							val = param.getValue().getResult();
						}
						System.out.println(CurrentTime() + " ObservableValue; " + val);
						final Label label = new Label(val);
						try {
							label.setText(param.getValue().getResult());
							param.getValue().ResultProperty().addListener(new ChangeListener<String>() {
								@Override
								public void changed(ObservableValue<? extends String> observable, String oldValue,
										String newValue) {
									System.out.println(CurrentTime() + " ObservableValue222; " + newValue);
									Platform.runLater(() -> label.setText(newValue));
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
						}
						return new SimpleObjectProperty<Label>(label);
					}
				});
		// callback函数，解决tableview单行刷新-----> 失败 :JAVAFX无法在非ui线程运行时更新ui线程
		tableResponse.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<TableModel, Label>, ObservableValue<Label>>() {
					@Override
					public ObservableValue<Label> call(CellDataFeatures<TableModel, Label> param) {
						String val = "Empty";
						if (param != null && param.getValue() != null) {
							val = param.getValue().getResponse();
						}
						final Label label = new Label(val);
						try {
							label.setText(param.getValue().getResponse());
							param.getValue().ResponseProperty().addListener(new ChangeListener<String>() {
								@Override
								public void changed(ObservableValue<? extends String> observable, String oldValue,
										String newValue) {
									System.out.println(CurrentTime() + " ObservableValue222; " + newValue);
									Platform.runLater(() -> label.setText("-->" + newValue));
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
						}
						return new SimpleObjectProperty<Label>(label);
					}
				});
		lt.remove(0);
		CasesTable.setItems(lt);
	}

	@FXML
	public void testAll() throws InterruptedException {
		Task<Void> progressTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				for (int i = 0; i < CasesTable.getItems().size(); i++) {
					casename = lt.get(i).CasenameProperty().getValue();
					at = lt.get(i).ATCommandProperty().getValue();
					expe = lt.get(i).ExpectionProperty().getValue();
					sendAT(at);
					lt.get(i).setResponse(resp);
					System.out.println(CurrentTime() + " sendAT(at); " + at);
					if (resp.trim().endsWith("OK")) {
						lt.get(i).setResult("pass");
					} else {
						lt.get(i).setResult("fail");
					}
					int time = Integer.parseInt(timout.getValue());
					Thread.sleep(time);
					resp = "";
				}
				pauseBtton.setText("暂停");
				stopButton.setText("停止");
				return null;
			}
		};
		th = new Thread(progressTask);
		th.start();
		pauseBtton.setDisable(false);
		stopButton.setDisable(false);
	}

	@FXML
	public void runSelect() throws InterruptedException {
		Task<Void> progressTask2 = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				int size = lt.size();
				for (int i = 0; i < size; i++) {
					if (lt.get(i).isChecked()) {
						// CasesTable.refresh();
						casename = lt.get(i).CasenameProperty().getValue();
						at = lt.get(i).ATCommandProperty().getValue();
						expe = lt.get(i).ExpectionProperty().getValue();
						sendAT(at);
						lt.get(i).setResponse(resp);
						if (resp.trim().endsWith("OK")) {
							lt.get(i).setResult("pass");
						} else {
							lt.get(i).setResult("fail");
						}
						int time = Integer.parseInt(timout.getValue());
						Thread.sleep(time);
						resp = "";
					}
				}
				pauseBtton.setText("暂停");
				stopButton.setText("停止");
				return null;
			}
		};
		th = new Thread(progressTask2);
		th.start();
		pauseBtton.setDisable(false);
		stopButton.setDisable(false);
	}

	@FXML
	public void ClearTextArea() {
		ATResponse.clear();
	}

	@FXML
	public void ExportLog() throws IOException {
		String content = ATResponse.getText();
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"),
				new ExtensionFilter("Log Files", "*.log"), new ExtensionFilter("All Files", "*.*"));
		File file = fileChooser.showSaveDialog(newAlertDialog);
		if (file != null) {
			FileWriter fileWriter;
			fileWriter = new FileWriter(file);
			fileWriter.write(content);
			fileWriter.close();
			openStatus.setText("保存成功");
		}
	}

	@Override
	public void run() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void GenerateReport() {
		ExportExcel ee = new ExportExcel();
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Excel Files", "*.xls"),
				new ExtensionFilter("Excel Files", "*.xlsx"), new ExtensionFilter("All Files", "*.*"));
		File file = fileChooser.showSaveDialog(newAlertDialog);
		if (file != null) {
			try {
				ee.Export(file, "AT命令测试报告", lt);
				openStatus.setText("Excel导出成功");
			} catch (IOException e) {
				openStatus.setText("Excel导出失败");
				e.printStackTrace();
			}
		}
	}

	@FXML
	public void openhelpfile() throws IOException, URISyntaxException {
		Desktop.getDesktop().browse(new URI("http://blog.csdn.net/yimingt/article/details/70239796"));
	}

	@SuppressWarnings("deprecation")
	@FXML
	public void pause() {
		if (pauseBtton.getText().equals("暂停")) {
			th.suspend();
			pauseBtton.setText("继续");
		} else {
			th.resume();
			pauseBtton.setText("暂停");
		}
	}

	@SuppressWarnings("deprecation")
	@FXML
	public void stop() {
		th.stop();
		pauseBtton.setText("暂停");
		stopButton.setDisable(true);
		pauseBtton.setDisable(true);
	}
}