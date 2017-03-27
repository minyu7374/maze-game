//程序开始界面

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class StartUI extends JFrame implements ActionListener {

    private ImageIcon mouse = new ImageIcon("./mouse.gif");
    private JButton button1 = new JButton("24 X 24", mouse);
    private JButton button2 = new JButton("35 X 35", mouse);
    private JButton button3 = new JButton("46 X 46", mouse);
    private JButton button4 = new JButton("？", mouse);


    StartUI() {

        //将选择按钮包含在选择面板上
        JPanel choose = new JPanel();
        choose.setLayout(new GridLayout(2, 2));
        choose.add(button1);
        choose.add(button2);
        choose.add(button3);
        choose.add(button4);

        //注册侦听器
        button1.addActionListener(this);
        button2.addActionListener(this);
        button3.addActionListener(this);
        button4.addActionListener(this);

        //提示信息
        JPanel message = new JPanel() {   //匿名内部类
            protected void paintComponent(Graphics g) {
                setSize(200, 300);
                g.drawString("请选择场地大小", 100, 35);
            }
        };

        //主界面布局
        setLayout(new BorderLayout(120, 40));
        add(choose, BorderLayout.CENTER);
        add(message, BorderLayout.NORTH);
        add(new JPanel(), BorderLayout.EAST);
        add(new JPanel(), BorderLayout.WEST);
        add(new JPanel(), BorderLayout.SOUTH);

        //基本设置
        setTitle("迷宫游戏");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
    }

    public static void main(String[] args) {
        new StartUI();
    }

    //按钮事件处理
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button1) {
            dispose(); // setVisible(false);
            new map(18, 18);
        } else if (e.getSource() == button2) {
            dispose();  //setVisible(false);
            new map(25, 25);
        } else if (e.getSource() == button3) {
            dispose();  //setVisible(false);
            new map(40, 40);
        } else {
            getData();
        }
    }

    public void getData() {
        int m = 0, n = 0;
        String crowString;
        try {
            crowString = JOptionPane.showInputDialog("请输入自定义的行数（>5）");
            m = Integer.parseInt(crowString);
            crowString = JOptionPane.showInputDialog("请输入自定义的列数（>5）");
            n = Integer.parseInt(crowString);
            if (m <= 5 || n <= 5) throw new Exception();
            else {
                dispose();  //setVisible(false);
                new map(m, n);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "由于用户取消或输入不符合要求等原因，未正常创建迷宫。", "未创建迷宫！", JOptionPane.ERROR_MESSAGE);
        }

    }

}
