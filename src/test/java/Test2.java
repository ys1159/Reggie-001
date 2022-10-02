import java.util.Scanner;

import java.lang.Math;

public class Test2 {

    public static void main (String[] args)

    {

        int a,b,c;

        double s,p;

        Scanner rd = new Scanner(System.in);

        System.out.print("请输入三角形的第一个边长：");

        a=rd.nextInt();

        System.out.print("请输入三角形的第二个边长：");

        b=rd.nextInt();

        System.out.print("请输入三角形的第三个边长：");

        c=rd.nextInt();

        if(a+b>c&&b+c>a&&a+c>b)//判断是否可以形成三角形

        {

            p=(a+b+c)/2;//求半周长

            s=Math.sqrt(p*(p-a)*(p-b)*(p-c));//求面积

            System.out.print("该三角形的面积为："+s);

        }

        else

            System.out.print("该三角形不合法！！");

    }

}
