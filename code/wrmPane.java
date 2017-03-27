//显示墙、路、鼠和粮仓的面板


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class wrmPane extends JPanel implements MouseListener {
    private boolean changeable_click;
    private int flag;  //标志 0：墙 1：路 2：鼠 3：粮仓
    private Image wall = new ImageIcon("./wall.gif").getImage();        //墙
    private Image road = new ImageIcon("./road.gif").getImage();        //路
    private Image mouse = new ImageIcon("./mouse.gif").getImage();      //鼠
    private Image liangc = new ImageIcon("./liangc.gif").getImage();   //粮仓

    wrmPane(int f) {
        flag = f;
        changeable_click = false;    //初始化时不能通过鼠标点击改变 flag 的值
        addMouseListener(this);
    }

    wrmPane() {
        this(0);
    }

    //重写paintComponent方法，画图
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (flag == 0)
            g.drawImage(wall, 0, 0, getWidth(), getHeight(), this);
        else if (flag == 1)
            g.drawImage(road, 0, 0, getWidth(), getHeight(), this);
        else if (flag == 2)
            g.drawImage(mouse, 0, 0, getWidth(), getHeight(), this);
        else
            g.drawImage(liangc, 0, 0, getWidth(), getHeight(), this);
    }

    //访问器
    public int getFlag() {
        return flag;
    }

    //是否为墙
    public boolean isWall() {
        return flag == 0;
    }

    //是否可通过点击实现墙路互变
    public boolean isChangeable() {
        return changeable_click;
    }

    //设置为是否能墙路互变
    public void setChangeable_click(boolean c) {
        changeable_click = c;
    }

    //修改标志并重画面板
    public void change(int f) {
        flag = f;
        repaint();
    }

    //鼠标事件处理
    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
        if (!changeable_click) return;
        if (flag == 0) {
            flag = 1;
            repaint();
        } else {
            flag = 0;
            repaint();
        }
    }

}
