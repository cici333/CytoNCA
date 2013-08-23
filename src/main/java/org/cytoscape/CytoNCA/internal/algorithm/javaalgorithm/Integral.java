/*
 * 计算数值积分的类 Integral
 * 
 * 周长发编制
 */
package org.cytoscape.CytoNCA.internal.algorithm.javaalgorithm;

/**
 * 计算数值积分的类 Integral
 *
 * @author 周长发
 * @version 1.0
 */
public abstract class Integral 
{
	/**
	 * 抽象函数：计算积分函数值，必须在派生类中覆盖该函数
	 * 
	 * @param x - 函数变量
	 * @return double型，对应的函数值
	 */
	public abstract double func(double x);

	/**
	 * 基本构造函数
	 */
	public Integral()
	{
	}

	/**
	 * 变步长梯形求积法
	 * 
	 * 调用时，须覆盖计算函数f(x)值的虚函数double func(double x)
	 * 
	 * @param a - 积分下限
	 * @param b - 积分上限，要求b>a
	 * @param eps - 积分精度要求
	 * @return double 型，积分值
	 */
	public double getValueTrapezia(double a, double b, double eps)
	{
		int n,k;
	    double fa,fb,h,t1,p,s,x,t=0;
	    
		// 积分区间端点的函数值
		fa=func(a); 
		fb=func(b);
	    
		// 迭代初值
		n=1; 
		h=b-a;
	    t1=h*(fa+fb)/2.0;
	    p=eps+1.0;

		// 迭代计算
	    while (p>=eps)
	    { 
			s=0.0;
	        for (k=0;k<=n-1;k++)
	        { 
				x=a+(k+0.5)*h;
	            s=s+func(x);
	        }
	        
			t=(t1+h*s)/2.0;
	        p=Math.abs(t1-t);
	        t1=t; 
			n=n+n; 
			h=h/2.0;
	    }
	    
		return(t);
	}

	/**
	 * 变步长辛卜生求积法
	 * 
	 * 调用时，须覆盖计算函数f(x)值的虚函数double func(double x)
	 * 
	 * @param a - 积分下限
	 * @param b - 积分上限，要求b>a
	 * @param eps - 积分精度要求
	 * @return double 型，积分值
	 */
	public double getValueSimpson(double a, double b, double eps)
	{ 
	    int n,k;
	    double h,t1,t2,s1,s2=0,ep,p,x;

		// 计算初值
	    n=1; 
		h=b-a;
	    t1=h*(func(a)+func(b))/2.0;
	    s1=t1;
	    ep=eps+1.0;
	    
		// 迭代计算
		while (ep>=eps)
	    { 
			p=0.0;
	        for (k=0;k<=n-1;k++)
	        { 
				x=a+(k+0.5)*h;
	            p=p+func(x);
	        }
	        
			t2=(t1+h*p)/2.0;
	        s2=(4.0*t2-t1)/3.0;
	        ep=Math.abs(s2-s1);
	        t1=t2; s1=s2; n=n+n; h=h/2.0;
	    }
	    
		return(s2);
	}

	/**
	 * 自适应梯形求积法
	 * 
	 * 调用时，须覆盖计算函数f(x)值的虚函数double func(double x)
	 * 
	 * @param a - 积分下限
	 * @param b - 积分上限，要求b>a
	 * @param d - 对积分区间进行分割的最小步长，当子区间的宽度
	 *            小于d时，即使没有满足精度要求，也不再往下进行分割
	 * @param eps - 积分精度要求
	 * @return double 型，积分值
	 */
	public double getValueATrapezia(double a, double b, double d, double eps)
	{ 
	    double h,f0,f1,t0,z;
	    double[] t = new double[2];

		// 迭代初值
	    h=b-a; 
		t[0]=0.0;
	    f0=func(a); 
		f1=func(b);
	    t0=h*(f0+f1)/2.0;

		// 递归计算
	    ppp(a,b,h,f0,f1,t0,eps,d,t);

	    z=t[0]; 
		
		return(z);
	}

	/**
	 * 内部函数
	 */
	private void ppp(double x0, double x1, double h, double f0, double f1, double t0, double eps, double d, double[] t)
	{ 
	    double x,f,t1,t2,p,g,eps1;

	    x=x0+h/2.0; 
		f=func(x);
	    t1=h*(f0+f)/4.0; 
		t2=h*(f+f1)/4.0;
	    p=Math.abs(t0-(t1+t2));
	    
		if ((p<eps)||(h/2.0<d))
	    { 
			t[0]=t[0]+(t1+t2); 
			return;
		}
	    else
	    { 
			g=h/2.0; 
			eps1=eps/1.4;
			// 递归
	        ppp(x0,x,g,f0,f,t1,eps1,d,t);
	        ppp(x,x1,g,f,f1,t2,eps1,d,t);
	        return;
	    }
	}

	/**
	 * 龙贝格求积法
	 * 
	 * 调用时，须覆盖计算函数f(x)值的虚函数double func(double x)
	 * 
	 * @param a - 积分下限
	 * @param b - 积分上限，要求b>a
	 * @param eps - 积分精度要求
	 * @return double 型，积分值
	 */
	public double getValueRomberg(double a, double b, double eps)
	{ 
	    int m,n,i,k;
	    double h,ep,p,x,s,q=0;
	    double[] y = new double[10];

		// 迭代初值
	    h=b-a;
	    y[0]=h*(func(a)+func(b))/2.0;
	    m=1; 
		n=1; 
		ep=eps+1.0;
	    
		// 迭代计算
		while ((ep>=eps)&&(m<=9))
	    { 
			p=0.0;
	        for (i=0;i<=n-1;i++)
	        { 
				x=a+(i+0.5)*h;
	            p=p+func(x);
	        }
	        
			p=(y[0]+h*p)/2.0;
	        s=1.0;
	        for (k=1;k<=m;k++)
	        { 
				s=4.0*s;
	            q=(s*p-y[k-1])/(s-1.0);
	            y[k-1]=p; p=q;
	        }

	        ep=Math.abs(q-y[m-1]);
	        m=m+1; 
			y[m-1]=q; 
			n=n+n; 
			h=h/2.0;
	    }
	    
		return(q);
	}

	/**
	 * 计算一维积分的连分式法
	 * 
	 * 调用时，须覆盖计算函数f(x)值的虚函数double func(double x)
	 * 
	 * @param a - 积分下限
	 * @param b - 积分上限，要求b>a
	 * @param eps - 积分精度要求
	 * @return double 型，积分值
	 */
	public double getValuePq(double a, double b, double eps)
	{ 
	    int m,n,k,l,j;
	    double hh,t1,s1,ep,s,x,t2,g=0;
	    double[] h = new double[10];
	    double[] bb = new double[10];

		// 迭代初值
	    m=1; 
		n=1;
	    hh=b-a; 
		h[0]=hh;
	    t1=hh*(func(a)+func(b))/2.0;
	    s1=t1; 
		bb[0]=s1; 
		ep=1.0+eps;
	    
		// 迭代计算
		while ((ep>=eps)&&(m<=9))
	    { 
			s=0.0;
	        for (k=0;k<=n-1;k++)
	        { 
				x=a+(k+0.5)*hh;
	            s=s+func(x);
	        }
	        
			t2=(t1+hh*s)/2.0;
	        m=m+1;
	        h[m-1]=h[m-2]/2.0;
	        g=t2;
	        l=0; 
			j=2;
	        
			while ((l==0)&&(j<=m))
	        { 
				s=g-bb[j-2];
	            if (Math.abs(s)+1.0==1.0) 
					l=1;
	            else 
					g=(h[m-1]-h[j-2])/s;
	            
				j=j+1;
	        }
	        
			bb[m-1]=g;
	        if (l!=0) 
				bb[m-1]=1.0e+35;
	        
			g=bb[m-1];
	        for (j=m;j>=2;j--)
	           g=bb[j-2]-h[j-2]/g;
	        
			ep=Math.abs(g-s1);
	        s1=g; 
			t1=t2; 
			hh=hh/2.0; 
			n=n+n;
	    }
	    
		return(g);
	}

	/**
	 * 高振荡函数求积法
	 * 
	 * 调用时，须覆盖计算函数f(x)值的虚函数double func(double x)
	 * 
	 * @param a - 积分下限
	 * @param b - 积分上限，要求b>a
	 * @param m - 被积函数中振荡函数的角频率
	 * @param n - 给定积分区间两端点上的导数最高阶数＋1
	 * @param fa - 一维数组，长度为n，存放f(x)在积分区间端点x=a处的各阶导数值
	 * @param fb - 一维数组，长度为n，存放f(x)在积分区间端点x=b处的各阶导数值
	 * @param s - 一维数组，长度为2，其中s(1)返回f(x)cos(mx)在积分区间的积分值，
	 *            s(2) 返回f(x)sin(mx)在积分区间的积分值
	 * @return double 型，积分值
	 */
	public double getValuePart(double a, double b, int m, int n, double[] fa, double[] fb, double[] s)
	{ 
		int mm,k,j;
	    double sma,smb,cma,cmb;
	    double[] sa = new double[4];
	    double[] sb = new double[4];
	    double[] ca = new double[4];
	    double[] cb = new double[4];
	    
		// 三角函数值
		sma=Math.sin(m*a); 
		smb=Math.sin(m*b);
	    cma=Math.cos(m*a); 
		cmb=Math.cos(m*b);
	    
		// 迭代初值
		sa[0]=sma; 
		sa[1]=cma; 
		sa[2]=-sma; 
		sa[3]=-cma;
	    sb[0]=smb; 
		sb[1]=cmb; 
		sb[2]=-smb; 
		sb[3]=-cmb;
	    ca[0]=cma; 
		ca[1]=-sma; 
		ca[2]=-cma; 
		ca[3]=sma;
	    cb[0]=cmb; 
		cb[1]=-smb; 
		cb[2]=-cmb; 
		cb[3]=smb;
	    s[0]=0.0; 
		s[1]=0.0;
	    
		mm=1;
	    
		// 循环迭代
		for (k=0;k<=n-1;k++)
	    { 
			j=k;
	        while (j>=4) 
				j=j-4;
	        
			mm=mm*m;
	        s[0]=s[0]+(fb[k]*sb[j]-fa[k]*sa[j])/(1.0*mm);
	        s[1]=s[1]+(fb[k]*cb[j]-fa[k]*ca[j])/(1.0*mm);
	    }
	    
		s[1]=-s[1];

		return s[0];
	}

	/**
	 * 勒让德－高斯求积法
	 * 
	 * 调用时，须覆盖计算函数f(x)值的虚函数double func(double x)
	 * 
	 * @param a - 积分下限
	 * @param b - 积分上限，要求b>a
	 * @param eps - 积分精度要求
	 * @return double 型，积分值
	 */
	public double getValueLegdGauss(double a, double b, double eps)
	{ 
	    int m,i,j;
	    double s,p,ep,h,aa,bb,w,x,g=0;

		// 勒让德－高斯求积系数
	    double[] t={-0.9061798459,-0.5384693101,0.0,
	                         0.5384693101,0.9061798459};
	    double[] c={0.2369268851,0.4786286705,0.5688888889,
	                        0.4786286705,0.2369268851};

		// 迭代初值
	    m=1;
	    h=b-a; 
		s=Math.abs(0.001*h);
	    p=1.0e+35; 
		ep=eps+1.0;
	    
		// 迭代计算
		while ((ep>=eps)&&(Math.abs(h)>s))
	    { 
			g=0.0;
	        for (i=1;i<=m;i++)
	        { 
				aa=a+(i-1.0)*h; 
				bb=a+i*h;
	            w=0.0;

	            for (j=0;j<=4;j++)
	            { 
					x=((bb-aa)*t[j]+(bb+aa))/2.0;
	                w=w+func(x)*c[j];
	            }
	            
				g=g+w;
	        }
	        
			g=g*h/2.0;
	        ep=Math.abs(g-p)/(1.0+Math.abs(g));
	        p=g; 
			m=m+1; 
			h=(b-a)/m;
	    }
	    
		return(g);
	}

	/**
	 * 拉盖尔－高斯求积法
	 * 
	 * 调用时，须覆盖计算函数f(x)值的虚函数double func(double x)
	 * 
	 * @return double 型，积分值
	 */
	public double getValueLgreGauss()
	{ 
		int i;
	    double x,g;

		// 拉盖尔－高斯求积系数
	    double[] t={0.26355990, 1.41340290, 3.59642600, 7.08580990, 12.64080000};
	    double[] c={0.6790941054, 1.638487956, 2.769426772, 4.315944000, 7.104896230};

		// 循环计算
	    g=0.0;
	    for (i=0; i<=4; i++)
	    { 
			x=t[i]; 
			g=g+c[i]*func(x); 
		}
	    
		return(g);
	}

	/**
	 * 埃尔米特－高斯求积法
	 * 
	 * 调用时，须覆盖计算函数f(x)值的虚函数double func(double x)
	 * 
	 * @return double 型，积分值
	 */
	public double getValueHermiteGauss()
	{ 
		int i;
	    double x,g;
		
		// 埃尔米特－高斯求积系数
	    double[] t={-2.02018200, -0.95857190, 0.0,0.95857190, 2.02018200};
	    double[] c={1.181469599, 0.9865791417, 0.9453089237, 0.9865791417, 1.181469599};

		// 循环计算
	    g=0.0;
	    for (i=0; i<=4; i++)
	    { 
			x=t[i]; 
			g=g+c[i]*func(x); 
		}
	    
		return(g);
	}
}
