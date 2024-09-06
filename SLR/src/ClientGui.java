import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;



public class ClientGui{
	public static void main(String[] args){
		LexFrame lexframe = new LexFrame();
		lexframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		lexframe.setResizable(false);
		lexframe.setVisible(true);
		AnalyseList analyse = new AnalyseList();

		//System.out.println(analyse.getactionAndGoto());
	}
	public ClientGui() {
		// TODO Auto-generated constructor stub

	}
}
class LexFrame extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;

	private JPanel main_panel;
	private DefaultTableModel process;

	private DefaultTableModel analyse;
	private JMenuBar main_menu_bar;
	private JMenu menu_file;
	private JMenu menu_run;
	private JMenuItem file_open;
	private JMenuItem file_save;
	private JMenuItem file_saveas;
	private JMenuItem exit;
	private JMenuItem run_lex;

	private JLabel lb_lex_result;
	private JLabel lb_lex_error;
	private JLabel error;
	private JLabel lb_text_edit;

	private JLabel lb_lex_any;

	private JButton btn_start_lex;
	private JButton btn_cleardata;
	private JTextArea ta_input;
	private JScrollPane scrollpane_input;


	private JTable tb_lex_result;
	private JTable tb_lex_nay;

	private DefaultTableModel tbmodel_lex_result;
	private DefaultTableModel errorlist;
	private JTable tberror;
	private JScrollPane scrollpane_lex_result;
	private JTable tb_lex_error;
	private DefaultTableModel tbmodel_lex_error;
	private JScrollPane scrollpane_lex_error;
	private JScrollPane scrollpane_lex_any;
	private final HashMap<Integer, HashMap<String, Action>> actionAndGoto;
	ArrayList<Production> productions; //产生式集


	ArrayList<Integer> rowNumber;

	public LexFrame(){
		this.setTitle("语义分析器");
		this.setSize(1310,1430);
		AnalyseList analyse = new AnalyseList();
		actionAndGoto = analyse.getactionAndGoto();
		initPanel();
		productions=analyse.getproductions();
	}
	public void initPanel(){
		main_menu_bar = new JMenuBar();
		menu_file = new JMenu("文件");
		menu_run = new JMenu("运行");

		file_open = new JMenuItem("打开");
		file_save = new JMenuItem("保存");
		file_saveas = new JMenuItem("另存为");
		exit = new JMenuItem("退出");
		file_open.addActionListener(this);
		exit.addActionListener(this);
		menu_file.add(file_open);
		menu_file.add(file_save);
		menu_file.add(file_saveas);
		menu_file.add(exit);
		main_menu_bar.add(menu_file);

		run_lex = new JMenuItem("语义分析");

		run_lex.addActionListener(this);
		menu_run.add(run_lex);
		main_menu_bar.add(menu_run);
		this.setJMenuBar(main_menu_bar);
		main_panel = new JPanel();
		lb_lex_result = new JLabel("词法分析结果");
		lb_lex_result.setFont(lb_lex_result.getFont().deriveFont(20f));
		main_panel.add(lb_lex_result);
		lb_lex_result.setBounds(10, 10, 400, 20);

		tbmodel_lex_result = new DefaultTableModel(null, new String[]{"名称", "值"});

		tb_lex_result = new JTable(tbmodel_lex_result);
		tb_lex_result.setEnabled(false);
		scrollpane_lex_result = new JScrollPane(tb_lex_result);
		main_panel.add(scrollpane_lex_result);
		scrollpane_lex_result.setBounds(10, 40, 650, 200);


		main_panel.setLayout(null);
		lb_text_edit = new JLabel("文本编辑区");
		lb_text_edit.setFont(lb_text_edit.getFont().deriveFont(20f));
		main_panel.add(lb_text_edit);
		lb_text_edit.setBounds(700, 10, 600, 20);

		ta_input = new JTextArea();
		scrollpane_input = new JScrollPane(ta_input);
		ta_input.setFont(ta_input.getFont().deriveFont(14f));
		main_panel.add(scrollpane_input);
		scrollpane_input.setBounds(700, 40, 550, 200);
		LineNumberHeaderView lineNumberHeader = new LineNumberHeaderView();
		Font font = new Font("Arial", Font.PLAIN, 16);
		lineNumberHeader.setFont(font);
		scrollpane_input.setRowHeaderView(lineNumberHeader);

		lb_lex_error = new JLabel("语法分析过程");
		lb_lex_error.setFont(lb_lex_error.getFont().deriveFont(20f));
		main_panel.add(lb_lex_error);
		lb_lex_error.setBounds(600, 240, 200, 20);

		process = new DefaultTableModel(null, new String[]{"symbols","action"});
		tb_lex_error = new JTable(process);
		tb_lex_error.setForeground(Color.BLUE);
		tb_lex_error.setEnabled(false);
		scrollpane_lex_error = new JScrollPane(tb_lex_error);
		main_panel.add(scrollpane_lex_error);
		JTableHeader head = tb_lex_error.getTableHeader(); // 创建表格标题对象
		head.setPreferredSize(new Dimension(head.getWidth(), 35));
		head.setFont(new Font("楷体", Font.PLAIN, 18));// 设置表格字体
		scrollpane_lex_error.setBounds(10, 270, 1240, 180);

		lb_lex_any = new JLabel("语义分析");
		lb_lex_any.setFont(lb_lex_any.getFont().deriveFont(20f));
		main_panel.add(lb_lex_any);
		lb_lex_any.setBounds(600, 450, 200, 20);
//
		analyse = new DefaultTableModel(null, new String[]{"address","commend"});
		tb_lex_nay = new JTable(analyse);
		tb_lex_nay.setForeground(Color.BLUE);
		tb_lex_nay.setEnabled(false);
		scrollpane_lex_any = new JScrollPane(tb_lex_nay);
		main_panel.add(scrollpane_lex_any);
		JTableHeader head2 = tb_lex_nay.getTableHeader();
		head2.setPreferredSize(new Dimension(head2.getWidth(), 35));
		head2.setFont(new Font("楷体", Font.PLAIN, 18));// 设置表格字体
		scrollpane_lex_any.setBounds(10, 490, 1240, 180);

		error = new JLabel("错误分析");
		error.setFont(error.getFont().deriveFont(20f));
		main_panel.add(error);
		error.setBounds(600, 680, 200, 20);

		errorlist = new DefaultTableModel(null, new String[]{"line", "error"});
		tberror = new JTable(errorlist);
		tberror.setForeground(Color.red);
		tberror.setEnabled(false);
		scrollpane_lex_error = new JScrollPane(tberror);
		main_panel.add(scrollpane_lex_error);
		JTableHeader head1 = tberror.getTableHeader();
		head1.setPreferredSize(new Dimension(head1.getWidth(), 35));
		head1.setFont(new Font("楷体", Font.PLAIN, 18));
		scrollpane_lex_error.setBounds(10, 710, 1240, 180);

		btn_start_lex = new JButton("语义分析");
		main_panel.add(btn_start_lex);
		btn_start_lex.setBounds(480, 670, 100, 40);
		//btn_start_lex.setBounds(480, 620, 200, 20);
		btn_start_lex.addActionListener(this);

		btn_cleardata = new JButton("清空");
		main_panel.add(btn_cleardata);
		btn_cleardata.setBounds(680, 900, 100, 40);
		//btn_cleardata.setBounds(680, 620, 200, 20);
		btn_cleardata.addActionListener(this);
		add(main_panel);

	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if ((e.getSource() == btn_start_lex) || (e.getSource() == run_lex)) {
			// 进行判定k
			clearTableData();
			if(ta_input.getText().equals("")){
				JOptionPane.showMessageDialog(main_panel, "您什么都没有输入啊！", "提示", JOptionPane.ERROR_MESSAGE);
				System.out.println("nothing input!");
			}
			else {
				// 词法分析
				TextLex text_lex = new TextLex(ta_input.getText(), tbmodel_lex_result, tbmodel_lex_error);
				rowNumber = text_lex.rowNumber;
                try {
                    text_lex.scanEntireText();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                //TODO 添加row

				// 获得结果的表
				ArrayList<String> symbolStack=text_lex.getSymbolStack();
				ArrayList<String> lex_result_stack = text_lex.get_Lex_Result();
				ArrayList<HashMap<String, String>> lex_error_stack = text_lex.get_Lex_Error();

				// 若是存在词法分析错误
				if(lex_error_stack.size()!=0){
					JOptionPane.showMessageDialog(main_panel, "词法分析阶段出现错误！", "提示", JOptionPane.ERROR_MESSAGE);
				}
				else {
					// 句法分析
					//System.out.print(actionAndGoto);
					//TODO textParse
					TextParse textParse = new TextParse(lex_result_stack, tbmodel_lex_error,actionAndGoto,productions,rowNumber,process,analyse,errorlist,symbolStack);
					File file = new File("result.txt");
					if (file.exists()){
						file.delete();
					}else {
						try {
							file.createNewFile();
						} catch (IOException ex) {
							throw new RuntimeException(ex);
						}
					}
					file = new File("error.txt");
					if (file.exists()){
						file.delete();
					}else {
						try {
							file.createNewFile();
						} catch (IOException ex) {
							throw new RuntimeException(ex);
						}
					}
					textParse.Parsing();
				}

			}
		}
		else if(e.getSource() == btn_cleardata){
			ta_input.setText("");
			clearTableData();
		}
		else if(e.getSource() == file_open){
			String file_name;
			JFileChooser file_open_filechooser = new JFileChooser();
			file_open_filechooser.setCurrentDirectory(new File("."));
			file_open_filechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int result = file_open_filechooser.showOpenDialog(main_panel);
			// 证明有选择
			if (result==JFileChooser.APPROVE_OPTION) {
				file_name = file_open_filechooser.getSelectedFile().getPath();
				// 读取文件，写到JTextArea里面
				File file = new File(file_name);
				try{
					InputStream in = new FileInputStream(file);
					int tempbyte;
					while ((tempbyte=in.read()) != -1) {
						ta_input.append(""+(char)tempbyte);
					}
					in.close();
				}
				catch(Exception event){
					event.printStackTrace();
				}
			}

		}
		else if(e.getSource() == exit){
			System.exit(1);
		}
		else {
			System.out.println("nothing！");
		}
	}

	public void clearTableData(){
//		System.out.println(tbmodel_lex_result.getRowCount());
		// 事先要是不给他们赋值的话就会造成，tbmodel_lex_error在删除的过程中会不断
		// 地减少，然后就会出现很蛋疼的删不干净的情况
		int error_rows = process.getRowCount();
		int result_rows = tbmodel_lex_result.getRowCount();
		int errorlist_row = errorlist.getRowCount();
		int any_row = analyse.getRowCount();
		for(int i=0;i<error_rows;i++)
		{
			process.removeRow(0);
			tb_lex_error.updateUI();
		}
		for(int i=0;i<any_row;i++)
		{
			analyse.removeRow(0);
			tb_lex_nay.updateUI();
		}
		for (int i=0;i<result_rows;i++){
			tbmodel_lex_result.removeRow(0);
			tb_lex_result.updateUI();
		}
		for (int i=0;i<errorlist_row;i++){
			errorlist.removeRow(0);
			tberror.updateUI();
		}

	}
}