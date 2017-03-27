/****************************************************************************************
 * *
 * *   此类中与图论有关的算法都将二维的坐标转化与之唯一对应的一维点，即
 * *       point[k]对应于tp[i][j]   则 i=point/n j=point%n  k=i*n+j
 * *
 * *   这些算法主要是
 * *      回溯法查找路径（深度优先搜索）               public static void findPath()
 * *      迷宫最短路径深度图算法                     public static void sortestPath()
 * *      生成随机迷宫时用的深度优先搜索算法          public static void DFS(int s)
 * *
 * *    另外引入以下重要概念
 * *       路径深度: 
 * *           从某位置走到出口的最短路径的长度，设每一方块为单位路径长度。
 * *           假定出口处路径深度为0 , 障碍处路径深度为 - 1 。
 * *       路径深度图: 
 * *           与迷宫对应的图, 每一个节点值为与该节点对应的迷宫单元格的路径深度
 * *
 *****************************************************************************************/


import java.io.*;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Operations {
    //数据同一包内类均可访问
    static int m, n;   //用于拷贝map.m和map.n的值
    static int m_currex, m_currey;   //老鼠当前位置
    static int m_startx, m_starty;   //老鼠开始位置
    static boolean changeable_key = false;     //是否可用键盘控制老鼠移动
    static boolean restart = false;

    private static boolean[] isBeVisit = null;//DFS生成随机迷宫专用数据

    //***************迷宫最短路径深度图算法
    @SuppressWarnings("deprecation")
    public static void sortestPath() {

        map.timeThread.suspend();    //时间控制线程休眠
        changeable_key = false;   //不可用键盘控制老鼠
        setEditable(false);       //不可编辑

        m = map.m;
        n = map.n;
        int max = m * n;                 //任意一点到粮仓的最短路径长度不会超出m*n。
        int[] depthGraph = new int[m * n];     //路径深度图

        //路径深度图初始化
        depthGraph[m * n - 1] = 0;                //粮仓到自己的距离自然是0
        for (int i = 0; i < m * n - 1; i++) {
            if (map.tp[i / n][i % n].isWall())
                depthGraph[i] = -1;           //墙表示为-1，表示无通路
            else
                depthGraph[i] = max;           //未确定距离时已max表示
        }


        boolean flag = true;             //循环过程中是否有某点的路径深度被修改
        int currex, currey;               //记录当前访问点的坐标
        int aroundmin;                   //周围可行方向的 最小路径深度 + 1

        //动态更新路径深度图直至其达到稳态（即最后一次循环过程中不再有路径深度被修改）
        while (flag) {
            flag = false;
            for (int s = m * n - 1; s >= 0; s--) {
                if (depthGraph[s] != -1) {
                    aroundmin = depthGraph[s];
                    currex = s / n;
                    currey = s % n;
                    if (currey + 1 < n && depthGraph[s + 1] != -1 && depthGraph[s + 1] + 1 < aroundmin)
                        aroundmin = depthGraph[s + 1] + 1;
                    if (currex + 1 < m && depthGraph[s + n] != -1 && depthGraph[s + n] + 1 < aroundmin)
                        aroundmin = depthGraph[s + n] + 1;
                    if (currey - 1 >= 0 && depthGraph[s - 1] != -1 && depthGraph[s - 1] + 1 < aroundmin)
                        aroundmin = depthGraph[s - 1] + 1;
                    if (currex - 1 >= 0 && depthGraph[s - n] != -1 && depthGraph[s - n] + 1 < aroundmin)
                        aroundmin = depthGraph[s - n] + 1;
                    if (aroundmin < depthGraph[s]) {
                        depthGraph[s] = aroundmin;
                        flag = true;
                    }

                }
            }
        }

        //利用已生成的路径深度图，找到从老鼠到粮仓之间的最短路径
        int[] path = new int[m * n];                    //用于存放最短路径的数组
        int currePoint = m_startx * n + m_starty;         //当前访问点,初始值为老鼠位置
        int depth = depthGraph[currePoint];         //老鼠位置的路径深度值
        int step = depth - 1;                     //当前要查找的路径深度
        while (step > 0) {
            currex = currePoint / n;
            currey = currePoint % n;
            if (currey + 1 < n && depthGraph[currePoint + 1] == step) {
                currePoint += 1;
            } else if (currex + 1 < m && depthGraph[currePoint + n] == step) {
                currePoint += n;
            } else if (currey - 1 >= 0 && depthGraph[currePoint - 1] == step) {
                currePoint -= 1;
            } else if (currex - 1 >= 0 && depthGraph[currePoint - n] == step) {
                currePoint -= n;
            }
            path[step--] = currePoint;
        }
        int s;         //临时存放位置
        for (int i = 1; i < depth; i++) {
            s = path[i];
            map.tp[s / n][s % n].change(2);   //显示最短路径
        }

        restart = true;                //可开始新游戏

    }


    //****************回溯法查找路径（深度优先搜索）
    @SuppressWarnings("deprecation")
    public static void findPath() {
        map.timeThread.suspend();    //时间控制线程休眠

        changeable_key = false;   //不可用键盘控制老鼠
        setEditable(false);       //不可编辑

        m = map.m;
        n = map.n;
        int currex = m_startx, currey = m_starty;
        int direction = 0;
        int distance;
        int[] point = new int[m * n];
        boolean[] isBeVisit = new boolean[m * n];
        for (int i = 0; i < m * n - 1; i++) isBeVisit[i] = false;
        int step = 0;
        point[step] = currex * n + currey;
        step++;
        for (; ; ) {
            if (currex == m - 1 && currey == n - 1) {      //已找到粮仓
                for (int i = 1; i < step - 1; i++)         //step-1对应粮仓，不再重画。
                    map.tp[point[i] / n][point[i] % n].change(2);
                restart = true;                //可开始新游戏
                return;
            }
            switch (direction) {       //按右0下1左2上3的优先顺序遍历
                case 0:
                    if (currey + 1 < n && !map.tp[currex][currey + 1].isWall() && !isBeVisit[currex * n + currey + 1]) {
                        point[step] = currex * n + currey + 1;
                        isBeVisit[currex * n + currey + 1] = true;
                        currey++;
                        step++;
                        direction = 0;

                    } else direction++;
                    break;
                case 1:
                    if (currex + 1 < m && !map.tp[currex + 1][currey].isWall() && !isBeVisit[(currex + 1) * n + currey]) {
                        point[step] = (currex + 1) * n + currey;
                        isBeVisit[(currex + 1) * n + currey] = true;
                        currex++;
                        step++;
                        direction = 0;

                    } else direction++;
                    break;
                case 2:
                    if (currey - 1 >= 0 && !map.tp[currex][currey - 1].isWall() && !isBeVisit[currex * n + currey - 1]) {
                        point[step] = currex * n + currey - 1;
                        isBeVisit[currex * n + currey - 1] = true;
                        currey--;
                        step++;
                        direction = 0;
                    } else direction++;
                    break;
                case 3:
                    if (currex - 1 >= 0 && !map.tp[currex - 1][currey].isWall() && !isBeVisit[(currex - 1) * n + currey]) {
                        point[step] = (currex - 1) * n + currey;
                        isBeVisit[(currex - 1) * n + currey] = true;
                        currex--;
                        step++;
                        direction = 0;
                    } else direction++;
                    break;
                default:              //此路不通，后退一步查看下一方向
                    step--;
                    isBeVisit[point[step]] = false;
                    if (step <= 0) {
                        JOptionPane.showMessageDialog(null, "抱歉，该迷宫没有通路，请使用其它迷宫开始新的游戏。", "该迷宫没有通路！", JOptionPane.ERROR_MESSAGE);
                        restart = true;
                        return;
                    }

                    distance = point[step] - point[step - 1];
                    if (distance == 1) {        //在上一步的右方向（0），则返回上一步时 再找下一个方向 direction=1   下同
                        currex = point[step - 1] / n;
                        currey = point[step - 1] % n;
                        direction = 1;
                    } else if (distance == n) {
                        currex = point[step - 1] / n;
                        currey = point[step - 1] % n;
                        direction = 2;
                    } else if (distance == -1) {
                        currex = point[step - 1] / n;
                        currey = point[step - 1] % n;
                        direction = 3;
                    } else {
                        direction = 4;       //继续后退
                    }
            }
        }
    }


    //深度优先遍历生成至少有一条随机通道的迷宫
    public static void creatMaze() {
        m = map.m;
        n = map.n;
        //遍历前初始化工作
        isBeVisit = new boolean[m * n];
        for (int i = 0; i < m * n; i++) isBeVisit[i] = false;      //是否已被访问

        //迷宫初始化
        for (int i = 0; i < m; i++) {                  //防止发生两边上全为墙的情况
            map.tp[i][0].change(Math.random() * 3 > 1 ? 0 : 1);
            map.tp[i][n - 1].change(Math.random() * 3 > 1 ? 0 : 1);
        }
        for (int i = 0; i < n; i++) {
            map.tp[0][i].change(Math.random() * 3 > 1 ? 0 : 1);
            map.tp[m - 1][i].change(Math.random() * 3 > 1 ? 0 : 1);
        }
        for (int i = 1; i < m - 1; i++)
            for (int j = 1; j < n - 1; j++)
                map.tp[i][j].change(0);  //内部的位置初始化全为墙

        m_startx = (int) (Math.random() * m / 2);
        m_starty = (int) (Math.random() * n / 2);    //随机生成老鼠位置

        //******************从老鼠位置开始深度优先遍历与它x 、y坐标相差均为偶数的点构成的图
        DFS(m_startx * n + m_starty);

        //*******************     这一步在 tp[m-2][n-2]与老鼠位置x 、y坐标相差均为偶数时非常重要，保证有到达粮仓的路径
        if (Math.random() * 2 > 1)
            map.tp[m - 2][n - 1].change(1);
        else
            map.tp[m - 1][n - 2].change(1);    //两者只要有一个为路即可，故随机取其一

        //老鼠和仓库的位置作另作处理
        map.tp[m_startx][m_starty].change(2);  //老鼠
        map.tp[m - 1][n - 1].change(3);            //粮仓

        changeable_key = false;  //键盘不可控制老鼠移动
        m_currex = m_startx;
        m_currey = m_starty;  //开始新游戏前老鼠当前位置与开始位置相等
        restart = false;
    }

    //****************从S点开始深度优先遍历与它x 、y坐标相差均为偶数的点构成的图，并打通每一步需要通过的墙
    public static void DFS(int s) {
        map.tp[s / n][s % n].change(1);
        isBeVisit[s] = true;

        int[] direction = new int[4];              //用于以随机顺序存储方向   右0下1左2上3
        boolean[] isStored = new boolean[4];
        for (int i = 0; i < 4; i++) isStored[i] = false;    //方向是否已被存储
        int currex = s / n, currey = s % n;                 //当前点对应的实际坐标

        //按随机顺序存储方向
        int rand, length = 0;      //随机数 用于产生随机顺序 ，length表示已存储方向的个数
        while (length < 4) {
            rand = (int) (Math.random() * 4);    //0~3
            if (!isStored[rand]) {
                direction[length++] = rand;
                isStored[rand] = true;            //修改为true，防止重复存储
            }
        }
        for (int i = 0; i < 4; i++) {
            switch (direction[i]) {
                case 0:
                    if (currey + 2 < n) {                             //右
                        if (!isBeVisit[s + 2]) {
                            map.tp[currex][currey + 1].change(1); //打通[currex][currey]与[currex][currey+2]之间的墙，下同
                            DFS(s + 2);
                        }
                    }
                    break;
                case 1:
                    if (currex + 2 < m) {                              //下
                        if (!isBeVisit[s + 2 * n]) {
                            map.tp[currex + 1][currey].change(1);
                            DFS(s + 2 * n);
                        }
                    }
                    break;
                case 2:                                               //左
                    if (currey - 2 >= 0) {
                        if (!isBeVisit[s - 2]) {
                            map.tp[currex][currey - 1].change(1);
                            DFS(s - 2);
                        }
                    }
                    break;
                case 3:                                            //上
                    if (currex - 2 >= 0) {
                        if (!isBeVisit[s - 2 * n]) {
                            map.tp[currex - 1][currey].change(1);
                            DFS(s - 2 * n);
                        }
                    }
                    break;
            }
        }
    }

    //开始游戏
    @SuppressWarnings("deprecation")
    public static void start() {
        if (restart) creatMaze();
        map.remaintime = map.timelimit;
        map.timeThread.resume();
        changeable_key = true;    //可用键盘控制老鼠
        setEditable(false);       //不可编辑
    }

    //设置时间
    @SuppressWarnings("deprecation")
    public static void setTime() {
        int time;
        String timeStr;
        try {
            timeStr = JOptionPane.showInputDialog("请输入最大时间限制（单位为秒)：\n提示：输入0代表无时间限制）");
            time = Integer.parseInt(timeStr);
            if (time < 0) throw new Exception();
            map.timelimit = time;   //设置完时间后重新开始游戏
            Object[] options = {"新游戏", "当前游戏"};
            int response = JOptionPane.showOptionDialog(null, "请选择是否开始新游戏还是重新玩当前游戏", "游戏时间设置成功", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (response == 0) {
                restart = true;
                start();
            } else if (response == 1) {
                map.tp[m_currex][m_currey].change(1);
                m_currex = m_startx;
                m_currey = m_starty;
                map.tp[m_currex][m_currey].change(2);
                restart = false;
                start();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "由于用户取消或输入不符合要求等原因，游戏时间限定设置失败。", "未更改游戏时间限制", JOptionPane.ERROR_MESSAGE);
            map.timeThread.resume(); //返回调用前状态
        }

    }


    //保存迷宫文件

    /*************************************************************
     *代码重写   2015/06/29
     *        由于之前的代码没考虑提示路径的情况下，整个老鼠经过的路都将被保留为老鼠，
     *    这样下次打开文件就会没法玩了，所以重新修改代码，仅保留初始位置的老鼠。
     *************************************************************/
    @SuppressWarnings("deprecation")
    public static void saveFile() {
        map.timeThread.suspend();    //时间控制线程休眠
        JFileChooser jfile = new JFileChooser();  //文件选择器
        jfile.setFileSelectionMode(0);            //文件选择器选择模式
        jfile.showSaveDialog(null);               //保存文件
        File file = jfile.getSelectedFile();       //选择文件

        if (file.exists()) {
            int anwser = JOptionPane.showConfirmDialog(null, "文件已存在，是否覆盖保存？", "文件已存在", JOptionPane.YES_NO_CANCEL_OPTION);
            if (anwser != JOptionPane.YES_OPTION) return;
        }

        try {
            String f = file.getAbsolutePath();                  //文件名
            RandomAccessFile out = new RandomAccessFile(f, "rw");
            out.writeInt(m);
            out.writeInt(n);
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (i == m_startx && j == m_starty)       //仅保留初始位置的老鼠
                        out.writeInt(2);
                    else if (map.tp[i][j].getFlag() == 2)     //其他位置老鼠保存为路
                        out.writeInt(1);
                    else
                        out.writeInt(map.tp[i][j].getFlag());
                }
            }
            out.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "未能保存文件，可能是用户取消或文件路径有误！", "错误提示", JOptionPane.ERROR_MESSAGE);
        } finally {
            map.timeThread.resume(); //时间控制仍是调用保存函数之前的状态
        }
    }

    //老鼠的移动  只有往右走和往下走才有可能到粮仓，因此只检测这两种情况是否成功进入粮仓
    public static void down() {
        if (!changeable_key) return;
        if (m_currex == m - 1 && m_currey == n - 1) return;
        if (m_currex + 1 == m - 1 && m_currey == n - 1) {
            map.tp[m_currex][m_currey].change(1);
            m_currex++;
            restart = true;
            int anwser = JOptionPane.showConfirmDialog(null, "恭喜你帮助老鼠成功进入粮仓，是否开始新的游戏。", "成功进入粮仓！", JOptionPane.YES_NO_CANCEL_OPTION);
            if (anwser == JOptionPane.YES_OPTION) start();
        } else if (m_currex + 1 < m && !map.tp[m_currex + 1][m_currey].isWall()) {
            map.tp[m_currex][m_currey].change(1);
            map.tp[++m_currex][m_currey].change(2);
        }
    }

    public static void up() {
        if (!changeable_key) return;
        if (m_currex == m - 1 && m_currey == n - 1) return;
        if (m_currex - 1 >= 0 && !map.tp[m_currex - 1][m_currey].isWall()) {
            map.tp[m_currex][m_currey].change(1);
            map.tp[--m_currex][m_currey].change(2);
        }
    }

    public static void left() {
        if (!changeable_key) return;
        if (m_currex == m - 1 && m_currey == n - 1) return;
        if (m_currey - 1 >= 0 && !map.tp[m_currex][m_currey - 1].isWall()) {
            map.tp[m_currex][m_currey].change(1);
            map.tp[m_currex][--m_currey].change(2);
        }
    }

    public static void right() {
        if (!changeable_key) return;
        if (m_currex == m - 1 && m_currey == n - 1) return;
        if (m_currex == m - 1 && m_currey + 1 == n - 1) {
            map.tp[m_currex][m_currey].change(1);
            m_currey++;
            restart = true;
            int anwser = JOptionPane.showConfirmDialog(null, "恭喜你帮助老鼠成功进入粮仓，是否开始新的游戏。", "成功进入粮仓！", JOptionPane.YES_NO_CANCEL_OPTION);
            if (anwser == JOptionPane.YES_OPTION) start();
        } else if (m_currey + 1 >= 0 && !map.tp[m_currex][m_currey + 1].isWall()) {
            map.tp[m_currex][m_currey].change(1);
            map.tp[m_currex][++m_currey].change(2);
        }
    }


    //设置是否能编辑
    public static void setEditable(boolean e) {
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                map.tp[i][j].setChangeable_click(e);
        //即使在编辑模式下老鼠和粮仓也不能修改
        map.tp[m - 1][n - 1].setChangeable_click(false);
        map.tp[m_startx][m_starty].setChangeable_click(false);
    }

    //自定义迷宫
    @SuppressWarnings("deprecation")
    public static void selfconfig() {
        map.timeThread.suspend();    //时间控制线程休眠
        changeable_key = false;      //不可用键盘移动老鼠
        setEditable(true);           //可以使墙变路、路变墙
        m_startx = m_currex;
        m_startx = m_currex;
        restart = false;               //保证开始游戏时使用的是编辑得到的迷宫
        map.timeThread.resume();   //时间控制回到调用函数前状态
    }

    //随机迷宫
    @SuppressWarnings("deprecation")
    public static void randommake() {
        map.timeThread.suspend();    //时间控制线程休眠
        creatMaze();
        changeable_key = false;
        setEditable(true);
        restart = false;        //保证开始游戏时使用的是编辑得到的迷宫
        map.timeThread.resume();   //时间控制回到调用函数前状态
    }

    //帮助文档
    @SuppressWarnings("deprecation")
    public static void showHelp() {
        map.timeThread.suspend();    //时间控制线程休眠
        String help = "本迷宫游戏非常简单，相信您很快就能知道它所有的功能，以下仅作出一些简\n单的说明：\n"
                + "一、程序启动时的主界面给出3种不同的游戏场地大小供用户选择，用户也可通\n      过点击？按钮来自定义场地大小。\n"
                + "二、进入游戏界面后，游戏所有操作选项都在菜单栏里，为方便您的使用，每个\n     选项快捷键都配有相应的快捷键。\n"
                + "三、开始游戏后，可用方向健控制老鼠的移动。\n"
                + "四、在编辑模式下，可以点击某一块区域使其变为墙或路。\n"
                + "五、除非开始游戏，在其它任何状态下均不可移动老鼠；同样除编辑模式外，其\n     它状态亦不可随意更改墙和路。\n"
                + "六、本程序以二进制文件方式读取和存储迷宫结构,因此，保存和打开文件时最好\n    选用.dat后缀名。\n"
                + "七、在此，十分感谢您的使用！";
        JOptionPane.showMessageDialog(null, help.toString(), "游戏使用说明", JOptionPane.INFORMATION_MESSAGE);
        map.timeThread.resume();    //时间控制回到调用函数前状态
    }

    //关于作者
    @SuppressWarnings("deprecation")
    public static void about() {
        map.timeThread.suspend();    //时间控制线程休眠
        String me = "   石河子大学信息科学与技术学院      \n\n"
                + "        计算机科学与技术专业          \n\n"
                + "            2012级2班                 \n\n"
                + "              王红涛                   \n\n";
        JOptionPane.showMessageDialog(null, me, "作者信息", JOptionPane.INFORMATION_MESSAGE);
        map.timeThread.resume();   //时间控制回到调用函数前状态
    }

}	 

	
	

