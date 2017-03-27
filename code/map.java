//迷宫界面


import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.RandomAccessFile;

import javax.swing.*;

@SuppressWarnings("serial")
public class map extends JFrame implements ActionListener, KeyListener, Runnable {
    static int m, n;
    static wrmPane[][] tp = null;        //显示动画,同一包内类均可访问

    //时间限制
    static Thread timeThread;                //时间控制线程
    static int timelimit, remaintime;
    static JPanel timePanel = new JPanel() {     //剩余时间显示面板
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            String rt;
            if (timelimit == 0) {
                rt = "无限制";
                setForeground(Color.GREEN);    //青色表示无时间限制
            } else {
                rt = remaintime / 3600 + " : " + (remaintime - (remaintime / 3600) * 3600) / 60 + " : " + remaintime % 60;
                if (remaintime > 10)
                    setForeground(Color.BLUE);      //剩余时间充足时为绿色
                else
                    setForeground(Color.RED);      //剩余时间很少时为红色
            }
            g.drawString("剩余时间：  " + rt, 220, 16);
        }
    };

    // 菜单项
    private JMenuItem m_start = new JMenuItem("开始新游戏（S）");
    private JMenuItem m_time = new JMenuItem("游戏时间限制（L）");
    private JMenuItem m_return = new JMenuItem("返回主界面（R）");
    private JMenuItem m_exit = new JMenuItem("退出游戏（Q）");
    private JMenuItem m_savefile = new JMenuItem("保存迷宫结构（W）");
    private JMenuItem m_importfile = new JMenuItem("导入迷宫结构（I）");
    private JMenuItem m_selfconfig = new JMenuItem("编辑当前迷宫（E）");
    private JMenuItem m_randommake = new JMenuItem("随机生成迷宫（Z）");
    private JMenuItem m_sortpath = new JMenuItem("显示最短路径（T）");
    private JMenuItem m_DFSpath = new JMenuItem("随意显示一个路径（K）");
    private JMenuItem m_help = new JMenuItem("游戏使用说明（H）");
    private JMenuItem m_about = new JMenuItem("关于迷宫游戏（A）");


    @SuppressWarnings("deprecation")
    map(int x, int y) {
        m = x;
        n = y;
        tp = new wrmPane[m][n];
        timelimit = remaintime = 0;                //初始化时，时间为0，代表没有时间限制

        timeThread = new Thread(this);
        timeThread.start();
        timeThread.suspend();

        //菜单
        JMenu game = new JMenu("游戏");
        JMenu file = new JMenu("文件");
        JMenu edit = new JMenu("编辑");
        JMenu tip = new JMenu("提示");
        JMenu help = new JMenu("帮助");
        game.add(m_start);
        game.add(m_time);
        game.add(m_return);
        game.add(m_exit);
        file.add(m_savefile);
        file.add(m_importfile);
        edit.add(m_selfconfig);
        edit.add(m_randommake);
        tip.add(m_sortpath);
        tip.add(m_DFSpath);
        help.add(m_help);
        help.add(m_about);

        //菜单栏
        JMenuBar menu = new JMenuBar();
        menu.add(game);
        menu.add(file);
        menu.add(edit);
        menu.add(tip);
        menu.add(help);

        //初始化迷宫组件,并生成随机路径
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++) {
                tp[i][j] = new wrmPane();
            }
        Operations.creatMaze();  //深度优先遍历生成至少有一条随机通道的迷宫

        //迷宫地图
        JPanel mazePane = new JPanel();
        mazePane.setLayout(new GridLayout(m, n, 0, 0));
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                mazePane.add(tp[i][j]);
            }
        }

        //菜单和时间显示放在同一面板上
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new GridLayout(1, 1));
        northPanel.add(menu);
        northPanel.add(timePanel);
        timePanel.setBackground(new Color(245, 240, 245));
        menu.setBackground(new Color(245, 240, 245));

        //添加到框架
        setLayout(new BorderLayout());
        add(northPanel, BorderLayout.NORTH);
        add(mazePane, BorderLayout.CENTER);
        add(new JPanel(), BorderLayout.SOUTH);

        //注册监听器
        m_start.addActionListener(this);
        m_time.addActionListener(this);
        m_return.addActionListener(this);
        m_exit.addActionListener(this);
        m_savefile.addActionListener(this);
        m_importfile.addActionListener(this);
        m_selfconfig.addActionListener(this);
        m_randommake.addActionListener(this);
        m_sortpath.addActionListener(this);
        m_DFSpath.addActionListener(this);
        m_help.addActionListener(this);
        m_about.addActionListener(this);
        addKeyListener(this);

        //基本设置
        setTitle("迷宫游戏");
        setSize(850, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);

    }

    map() {
        this(25, 25);
    }

    //导入迷宫结构
    @SuppressWarnings("deprecation")
    public void importFile() {
        map.timeThread.suspend();    //时间控制线程休眠
        JFileChooser jfile = new JFileChooser();  //文件选择器
        jfile.setFileSelectionMode(JFileChooser.FILES_ONLY);            //文件选择器选择模式
        jfile.showOpenDialog(null);               //打开文件
        File file = jfile.getSelectedFile();       //选择文件
        try {
            String f = file.getAbsolutePath();
            RandomAccessFile in = new RandomAccessFile(f, "rw");
            int newm = in.readInt(), newn = in.readInt();
            if (newm != m || newn != n) {
                dispose();
                new map(newm, newn);
            }
            int flag;
            for (int i = 0; i < m; i++)
                for (int j = 0; j < n; j++) {
                    flag = in.readInt();
                    if (flag == 2) {
                        Operations.m_startx = i;
                        Operations.m_starty = j;
                        Operations.m_currex = i;
                        Operations.m_currey = j;
                    }
                    tp[i][j].change(flag);
                }
            in.close();
            repaint();
            int anwser = JOptionPane.showConfirmDialog(null, "文件中的迷宫结构已导入成功，是否开始游戏？", "文件导入成功！", JOptionPane.YES_NO_CANCEL_OPTION);
            if (anwser == JOptionPane.YES_OPTION) Operations.start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "导入迷宫结构失败，可能是用户取消或文件不存在、文件数据有误等原因造成的。", "错误提示", JOptionPane.ERROR_MESSAGE);
        }
        Operations.changeable_key = false;      //不可用键盘移动老鼠
        Operations.restart = false;               //保证开始游戏时使用的是导入的的迷宫
    }

    //重写run方法，进行时间控制
    public void run() {
        if (timelimit > 0) {
            while (true) {
                try {
                    Thread.sleep(1000);
                    if (remaintime > 0)
                        remaintime--;
                    timePanel.repaint();
                    if (timelimit > 0 && remaintime == 0) {
                        if (Operations.m_currex != m - 1 || Operations.m_currey != n - 1) {
                            Object[] options = {"新游戏", "重来一次"};
                            int response = JOptionPane.showOptionDialog(this, "  很遗憾，你没有在限制的时间里完成任务，可怜的小老鼠已经饿死了\n请选择开始新的游戏，或重玩此游戏", "游戏超时！", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                            if (response == 0) {
                                Operations.restart = true;
                                Operations.start();
                            } else {
                                remaintime = timelimit;
                                tp[Operations.m_currex][Operations.m_currey].change(1);
                                Operations.m_currex = Operations.m_startx;
                                Operations.m_currey = Operations.m_starty;
                                tp[Operations.m_currex][Operations.m_currey].change(2);
                            }

                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }


    //菜单事件处理
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == m_start) {
            Operations.start();
        } else if (e.getSource() == m_return) {
            dispose(); //关闭当前窗口
            new StartUI();
        } else if (e.getSource() == m_DFSpath) {
            Operations.findPath();
        } else if (e.getSource() == m_exit) {
            System.exit(0);
        } else if (e.getSource() == m_selfconfig) {
            Operations.selfconfig();
        } else if (e.getSource() == m_randommake) {
            Operations.randommake();
        } else if (e.getSource() == m_help) {
            Operations.showHelp();
        } else if (e.getSource() == m_sortpath) {
            Operations.sortestPath();
        } else if (e.getSource() == m_about) {
            Operations.about();
        } else if (e.getSource() == m_savefile) {
            Operations.saveFile();
        } else if (e.getSource() == m_importfile) {
            importFile();
        } else if (e.getSource() == m_time) {
            Operations.setTime();
        }

    }

    //键盘事件处理
    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DOWN:
                Operations.down();
                break;
            case KeyEvent.VK_UP:
                Operations.up();
                break;
            case KeyEvent.VK_LEFT:
                Operations.left();
                break;
            case KeyEvent.VK_RIGHT:
                Operations.right();
                break;
            case KeyEvent.VK_S:
                Operations.start();
                break;
            case KeyEvent.VK_Q:
                System.exit(0);
                break;
            case KeyEvent.VK_R:
                dispose();
                new StartUI();
                break;
            case KeyEvent.VK_E:
                Operations.selfconfig();
                break;
            case KeyEvent.VK_Z:
                Operations.randommake();
                break;
            case KeyEvent.VK_K:
                Operations.findPath();
                break;
            case KeyEvent.VK_H:
                Operations.showHelp();
                break;
            case KeyEvent.VK_T:
                Operations.sortestPath();
                break;
            case KeyEvent.VK_A:
                Operations.about();
                break;
            case KeyEvent.VK_W:
                Operations.saveFile();
                break;
            case KeyEvent.VK_I:
                importFile();
                break;
            case KeyEvent.VK_L:
                Operations.setTime();
                break;
        }

    }

}
    
