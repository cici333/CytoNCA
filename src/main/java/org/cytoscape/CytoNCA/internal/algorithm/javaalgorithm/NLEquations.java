/*
 * 求解非线性方程组的类 NLEquations
 * 
 * 周长发编制
 */
package org.cytoscape.CytoNCA.internal.algorithm.javaalgorithm;

/**
 * 求解非线性方程组的类 NLEquations
 *
 * @author 周长发
 * @version 1.0
 */
public abstract class NLEquations 
{
	/**
	 * 虚函数：计算方程左端函数值，必须在引申类中覆盖该类函数
	 * 
	 * @param x - 变量
	 * @return 函数值
	 */
	protected double func(double x)
	{
		return 0;
	}
	
	/**
	 * 虚函数：计算方程左端函数值，必须在引申类中覆盖该类函数
	 * 
	 * @param x - 变量值数组
	 * @return 函数值
	 */
	protected double func(double[] x)
	{
		return 0;
	}


	/**
	 * 虚函数：计算方程左端函数值，必须在引申类中覆盖该类函数
	 * 
	 * @param x - 变量
	 * @param y - 函数值数组
	 */
	protected void func(double x, double[] y)
	{
	}
	
	/**
	 * 虚函数：计算方程左端函数值，必须在引申类中覆盖该类函数
	 * 
	 * @param x - 二元函数的变量
	 * @param y - 二元函数的变量
	 * @return 函数值
	 */
	protected double func(double x, double y)
	{
		return 0;
	}
	
	/**
	 * 虚函数：计算方程左端函数值，必须在引申类中覆盖该类函数
	 * 
	 * @param x - 二元函数的变量值数组
	 * @param y - 二元函数的变量值数组
	 * @return 函数值
	 */
	protected double func(double[] x, double[] y)
	{
		return 0;
	}
	
	/**
	 * 虚函数：计算方程左端函数值，必须在引申类中覆盖该类函数
	 * 
	 * @param x - 已知变量值数组
	 * @param p - 已知函数值数组
	 */
	protected void funcMJ(double[] x, double[] p)
	{
	}

	/**
     * 基本构造函数
     */
	 public NLEquations()
	 {
	 }

	 /**
	  * 求非线性方程实根的对分法
	  * 
	  * 调用时，须覆盖计算方程左端函数f(x)值的虚函数
	  *        double func(double x)
	  * 
	  * @param nNumRoots - 在[xStart, xEnd]内实根个数的预估值
	  * @param x - 一维数组，长度为m。返回在区间[xStart, xEnd]内搜索到的实根，
	  *            实根个数由函数值返回
	  * @param xStart - 求根区间的左端点
	  * @param xEnd - 求根区间的右端点
	  * @param dblStep - 搜索求根时采用的步长
	  * @param eps - 精度控制参数
	  * @return int 型，求得的实根的数目
	  */
	 public int getRootBisect(int nNumRoots, double[] x, double xStart, double xEnd, double dblStep, double eps)
	 {
	 	 int n,js;
	     double z,y,z1,y1,z0,y0;
	
	 	 // 根的个数清0
	     n = 0; 
	
	 	 // 从左端点开始搜索
	 	 z = xStart; 
	 	 y = func(z);
	
	 	 // 循环求解
	     while ((z<=xEnd+dblStep/2.0)&&(n!=nNumRoots))
	     { 
	 		if (Math.abs(y)<eps)
	         { 
	 			n=n+1; 
	 			x[n-1]=z;
	             z=z+dblStep/2.0; 
	 			y=func(z);
	         }
	         else
	         { 
	 			z1=z+dblStep; 
	 			y1=func(z1);
	             
	 			if (Math.abs(y1)<eps)
	             { 
	 				n=n+1; 
	 				x[n-1]=z1;
	                 z=z1+dblStep/2.0; 
	 				y=func(z);
	             }
	             else if (y*y1>0.0)
	             { 
	 				y=y1; 
	 				z=z1;
	 			}
	             else
	             { 
	 				js=0;
	                 while (js==0)
	                 { 
	 					if (Math.abs(z1-z)<eps)
	                     { 
	 						n=n+1; 
	 						x[n-1]=(z1+z)/2.0;
	                         z=z1+dblStep/2.0; y=func(z);
	                         js=1;
	                     }
	                     else
	                     { 
	 						z0=(z1+z)/2.0; 
	 						y0=func(z0);
	                         if (Math.abs(y0)<eps)
	                         { 
	 							x[n]=z0; 
	 							n=n+1; 
	 							js=1;
	                             z=z0+dblStep/2.0; 
	 							y=func(z);
	                         }
	                         else if ((y*y0)<0.0)
	                         { 
	 							z1=z0; 
	 							y1=y0;
	 						}
	                         else 
	 						{ 
	 							z=z0; 
	 							y=y0;
	 						}
	                     }
	                 }
	             }
	         }
	     }
	     
	 	// 返回实根的数目
	 	return(n);
	 }
	
	 /**
	  * 求非线性方程一个实根的牛顿法
	  * 
	  * 调用时，须覆盖计算方程左端函数f(x)及其一阶导数f'(x)值的虚函数:
	  * 	void func(double x, double[] y)
	  * 	y(0) 返回f(x)的值
	  * 	y(1) 返回f'(x)的值
	  * 
	  * @param x - 传入迭代初值（猜测解），返回在区间求得的一个实根
	  * @param nMaxIt - 递归次数
	  * @param eps - 精度控制参数
	  * @return boolean 型，求解是否成功
	  */
	 public boolean getRootNewton(Real x, int nMaxIt, double eps)
	 { 
	     int l;
	     double d,p,x0,x1=0.0;
	     double[] y = new double[2];
	
         // 条件值
	     l=nMaxIt; 
	     x0=x.doubleValue();
	     func(x0,y);
	     
	 	 // 求解，控制精度
	     d=eps+1.0;
	     while ((d>=eps)&&(l!=0))
	     { 
	 		 if (y[1] == 0.0)
	 			return false;
	
	         x1=x0-y[0]/y[1];
	         func(x1,y);
	         
	 		 d=Math.abs(x1-x0); 
	 		 p=Math.abs(y[0]);
	         if (p>d) 
	 			d=p;
	         x0=x1; 
	 		 l=l-1;
	     }
	     
	 	x.setValue(x1);
	
	 	return true;
	 }
	
	 /**
	  * 求非线性方程一个实根的埃特金迭代法
	  * 
	  * 调用时，须覆盖计算方程左端函数f(x)值的虚函数
	  *       double func(double x)
	  * 
	  * @param x - 传入迭代初值（猜测解），返回在区间求得的一个实根
	  * @param nMaxIt - 递归次数
	  * @param eps - 精度控制参数
	  * @return boolean 型，求解是否成功
	  */
	 public boolean getRootAitken(Real x, int nMaxIt, double eps)
	 { 
	 	int flag,l;
	    double u,v,x0;
	
	 	// 求解条件
	    l=0; 
	 	x0=x.doubleValue(); 
	 	flag=0;
	
	 	// 迭代求解
	     while ((flag==0)&&(l!=nMaxIt))
	     { 
	 		l=l+1; 
	        u=func(x0); 
	 		v=func(u);
	        if (Math.abs(u-v)<eps) 
	 		{ 
	 			x0=v; 
	 			flag=1; 
	 		}
	        else 
	 			x0=v-(v-u)*(v-u)/(v-2.0*u+x0);
	     }
	     
	 	x.setValue(x0); 
	     
	 	// 是否在指定的迭代次数内达到求解精度
	 	return (nMaxIt > l);
	 }
	
	 /**
	  * 求非线性方程一个实根的连分式解法
	  * 
	  * 调用时，须覆盖计算方程左端函数f(x)值的虚函数
	  *       double func(double x)
	  * 
	  * @param x - 传入迭代初值（猜测解），返回在区间求得的一个实根
	  * @param eps - 精度控制参数
	  * @return boolean 型，求解是否成功
	  */
	 public boolean getRootPq(Real x, double eps)
	 { 
	    int i,j,m,it=0,l;
	    double z,h,x0,q;
	    double[] a = new double[10];
	    double[] y = new double[10];
	
	 	// 求解条件
	    l=10; 
	 	q=1.0e+35; 
	 	x0=x.doubleValue(); 
	 	h=0.0;
	     
	 	// 连分式求解
	 	while (l!=0)
	    { 
	 		l=l-1; 
	 		j=0; 
	 		it=l;
	        while (j<=7)
	        { 
	 			if (j<=2) 
	 				z=x0+0.1*j;
	            else 
	 				z=h;
	 			
	 			y[j]=func(z);
	            h=z;
	            if (j==0) 
	 				a[0]=z;
	             else
	             { 
	 				m=0; 
	 				i=0;
	                while ((m==0)&&(i<=j-1))
	                { 
	 					if (Math.abs(h-a[i])+1.0==1.0) 
	 						m=1;
	                    else 
	 						h=(y[j]-y[i])/(h-a[i]);
	                      
	 					i=i+1;
	                 }
	                 a[j]=h;
	                 if (m!=0) 
	 					a[j]=q;
	                 h=0.0;
	                 for (i=j-1; i>=0; i--)
	                 { 
	 					if (Math.abs(a[i+1]+h)+1.0==1.0) 
	 						h=q;
	                    else 
	 						h=-y[i]/(a[i+1]+h);
	                 }
	                  
	 				h=h+a[0];
	             }
	              
	 			if (Math.abs(y[j])>=eps) 
	 				j=j+1;
	            else 
	 			{ 
	 				j=10; 
	 				l=0;
	 			}
	 		}
	         
	 		x0=h;
	 	}
	
	 	x.setValue(h);
	     
	 	// 是否在10阶连分式内求的实根？
	 	return (10>it);
	 }
	
	 /**
	  * 求实系数代数方程全部根的QR方法
	  * 
	  * @param n - 多项式方程的次数
	  * @param dblCoef - 一维数组，长度为n+1，按降幂次序依次存放n次多项式方程的
	  *                  n+1个系数
	  * @param xr - 一维数组，长度为n，返回n个根的实部
	  * @param xi - 一维数组，长度为n，返回n个根的虚部
	  * @param nMaxIt - 迭代次数
	  * @param eps - 精度控制参数
	  * @return boolean 型，求解是否成功
	  */
	 public boolean getRootQr(int n, double[] dblCoef, double[] xr, double[] xi, int nMaxIt, double eps)
	 { 
	 	// 初始化矩阵
	 	Matrix mtxQ = new Matrix();
	 	mtxQ.init(n, n);
	 	double[] q = mtxQ.getData();
	
	 	// 构造赫申伯格矩阵
	     for (int j=0; j<=n-1; j++)
	 		q[j]=-dblCoef[n-j-1]/dblCoef[n];
	
	     for (int j=n; j<=n*n-1; j++)
	 		q[j]=0.0;
	
	     for (int i=0; i<=n-2; i++)
	 		q[(i+1)*n+i]=1.0;
	
	 	// 求赫申伯格矩阵的特征值和特征向量，即为方程的解
	     if (mtxQ.computeEvHBerg(xr, xi, nMaxIt, eps))
	 		return true;
	
	 	return false;
	 }
	
	 /**
	  * 求实系数代数方程全部根的牛顿下山法
	  * 
	  * @param n - 多项式方程的次数
	  * @param dblCoef - 一维数组，长度为n+1，按降幂次序依次存放n次多项式方程的
	  *                  n+1个系数
	  * @param xr - 一维数组，长度为n，返回n个根的实部
	  * @param xi - 一维数组，长度为n，返回n个根的虚部
	  * @return boolean 型，求解是否成功
	  */
	 public boolean getRootNewtonDownHill(int n, double[] dblCoef, double[] xr, double[] xi)
	 { 
	 	int m=0,i=0,jt=0,k=0,is=0,it=0;
	    double t=0,x=0,y=0,x1=0,y1=0,dx=0,dy=0,p=0,q=0,w=0,dd=0,dc=0,c=0;
	    double g=0,u=0,v=0,pq=0,g1=0,u1=0,v1=0;
	     
	 	// 初始判断
	    m=n;
	    while ((m>0)&&(Math.abs(dblCoef[m])+1.0==1.0)) 
	 		m=m-1;
	
	 	// 求解失败
	    if (m<=0)
	 		return false;
	
	    for (i=0; i<=m; i++)
	 		dblCoef[i]=dblCoef[i]/dblCoef[m];
	     
	 	for (i=0; i<=m/2; i++)
	    { 
	 		w=dblCoef[i]; 
	 		dblCoef[i]=dblCoef[m-i]; 
	 		dblCoef[m-i]=w;
	 	}
	     
	 	// 迭代求解
	 	k=m; 
	 	is=0; 
	 	w=1.0;
	    jt=1;
	    while (jt==1)
	    { 
	 		pq=Math.abs(dblCoef[k]);
	 		while (pq<1.0e-12)
	        { 
	 			xr[k-1]=0.0; 
	 			xi[k-1]=0.0; 
	 			k=k-1;
	 			if (k==1)
	            { 
	 				xr[0]=-dblCoef[1]*w/dblCoef[0]; 
	 				xi[0]=0.0;
	                 
	 				return true;
	            }
	             
	 			pq=Math.abs(dblCoef[k]);
	        }
	 	
	 		q=Math.log(pq); 
	 		q=q/(1.0*k); 
	 		q=Math.exp(q);
	        p=q; 
	 		w=w*p;
	        for (i=1; i<=k; i++)
	        { 
	 			dblCoef[i]=dblCoef[i]/q; 
	 			q=q*p;
	 		}
	         
	 		x=0.0001; 
	 		x1=x; 
	 		y=0.2; 
	 		y1=y; 
	 		dx=1.0;
	        g=1.0e+37;
	        
	        while (true)
	        {
		         u=dblCoef[0]; v=0.0;
		         for (i=1; i<=k; i++)
		         { 
		 			p=u*x1; 
		 			q=v*y1;
		             pq=(u+v)*(x1+y1);
		             u=p-q+dblCoef[i]; 
		 			v=pq-p-q;
		         }
		         
		 		g1=u*u+v*v;
		         if (g1>=g)
		         { 
		 			if (is!=0)
		             { 
		 				 it=1;
		 				 Real xtmp = new Real(x);
		 				 Real ytmp = new Real(y);
		 				 Real x1tmp = new Real(x1);
		 				 Real y1tmp = new Real(y1);
		 				 Real dxtmp = new Real(dx);
		 				 Real dytmp = new Real(dy);
		 				 Real ddtmp = new Real(dd);
		 				 Real dctmp = new Real(dc);
		 				 Real ctmp = new Real(c);
		 				 Real ktmp = new Real(k);
		 				 Real istmp = new Real(is);
		 				 Real ittmp = new Real(it);
		                 g65(xtmp, ytmp, x1tmp, y1tmp, dxtmp, dytmp, ddtmp, dctmp, ctmp, ktmp, istmp, ittmp);
		                 x=xtmp.doubleValue();
						 y=ytmp.doubleValue();
						 x1=x1tmp.doubleValue();
						 y1=y1tmp.doubleValue();
						 dx=dxtmp.doubleValue();
						 dy=dytmp.doubleValue();
						 dd=ddtmp.doubleValue();
						 dc=dctmp.doubleValue();
						 c=ctmp.doubleValue();
						 k=ktmp.intValue(); 
						 is=istmp.intValue();
						 it=ittmp.intValue();
						 
		                 if (it==0) 
		 					continue;
		             }
		             else
		             { 
		 				 Real ttmp = new Real(t);
		 				 Real xtmp = new Real(x);
		 				 Real ytmp = new Real(y);
		 				 Real x1tmp = new Real(x1);
		 				 Real y1tmp = new Real(y1);
		 				 Real dxtmp = new Real(dx);
		 				 Real dytmp = new Real(dy);
		 				 Real ptmp = new Real(p);
		 				 Real qtmp = new Real(q);
		 				 Real ktmp = new Real(k);
		 				 Real ittmp = new Real(it);
		 				 g60(ttmp,xtmp,ytmp,x1tmp,y1tmp,dxtmp,dytmp,ptmp,qtmp,ktmp,ittmp);
		                 t=ttmp.doubleValue();
		                 x=xtmp.doubleValue();
						 y=ytmp.doubleValue();
						 x1=x1tmp.doubleValue();
						 y1=y1tmp.doubleValue();
						 dx=dxtmp.doubleValue();
						 dy=dytmp.doubleValue();
						 p=ptmp.doubleValue();
						 q=qtmp.doubleValue();
						 k=ktmp.intValue(); 
						 it=ittmp.intValue();
		 				 
		                 if (t>=1.0e-03) 
		 					continue;
		                 
		 				if (g>1.0e-18)
		                 { 
		 					 it=0;
			 				 Real xtmp1 = new Real(x);
			 				 Real ytmp1 = new Real(y);
			 				 Real x1tmp1 = new Real(x1);
			 				 Real y1tmp1 = new Real(y1);
			 				 Real dxtmp1 = new Real(dx);
			 				 Real dytmp1 = new Real(dy);
			 				 Real ddtmp1 = new Real(dd);
			 				 Real dctmp1 = new Real(dc);
			 				 Real ctmp1 = new Real(c);
			 				 Real ktmp1 = new Real(k);
			 				 Real istmp1 = new Real(is);
			 				 Real ittmp1 = new Real(it);
			                 g65(xtmp1, ytmp1, x1tmp1, y1tmp1, dxtmp1, dytmp1, ddtmp1, dctmp1, ctmp1, ktmp1, istmp1, ittmp1);
			                 x=xtmp1.doubleValue();
							 y=ytmp1.doubleValue();
							 x1=x1tmp1.doubleValue();
							 y1=y1tmp1.doubleValue();
							 dx=dxtmp1.doubleValue();
							 dy=dytmp1.doubleValue();
							 dd=ddtmp1.doubleValue();
							 dc=dctmp1.doubleValue();
							 c=ctmp1.doubleValue();
							 k=ktmp1.intValue(); 
							 is=istmp1.intValue();
							 it=ittmp1.intValue();
		                     if (it==0) 
		 						continue;
		                 }
		             }
		             
	 				 Real xtmp = new Real(x);
	 				 Real ytmp = new Real(y);
	 				 Real ptmp = new Real(p);
	 				 Real qtmp = new Real(q);
	 				 Real wtmp = new Real(w);
	 				 Real ktmp = new Real(k);
		 			 g90(xr,xi,dblCoef,xtmp,ytmp,ptmp,qtmp,wtmp,ktmp);
	                 x=xtmp.doubleValue();
					 y=ytmp.doubleValue();
					 p=ptmp.doubleValue();
					 q=qtmp.doubleValue();
					 w=wtmp.doubleValue();
					 k=ktmp.intValue(); 
		 			 break;
		         }
		         else
		         { 
		 			g=g1; 
		 			x=x1; 
		 			y=y1; 
		 			is=0;
		            if (g<=1.0e-22)
		            {
		 				 Real xtmp = new Real(x);
		 				 Real ytmp = new Real(y);
		 				 Real ptmp = new Real(p);
		 				 Real qtmp = new Real(q);
		 				 Real wtmp = new Real(w);
		 				 Real ktmp = new Real(k);
			 			 g90(xr,xi,dblCoef,xtmp,ytmp,ptmp,qtmp,wtmp,ktmp);
		                 x=xtmp.doubleValue();
						 y=ytmp.doubleValue();
						 p=ptmp.doubleValue();
						 q=qtmp.doubleValue();
						 w=wtmp.doubleValue();
						 k=ktmp.intValue(); 
		            }
		            else
		            { 
		 				u1=k*dblCoef[0]; 
		 				v1=0.0;
		                for (i=2; i<=k; i++)
		                { 
		 					p=u1*x; 
		 					q=v1*y; 
		 					pq=(u1+v1)*(x+y);
		                     u1=p-q+(k-i+1)*dblCoef[i-1];
		                     v1=pq-p-q;
		                 }
		                 
		 				p=u1*u1+v1*v1;
		                if (p<=1.0e-20)
		                { 
		 					 it=0;
			 				 Real xtmp = new Real(x);
			 				 Real ytmp = new Real(y);
			 				 Real x1tmp = new Real(x1);
			 				 Real y1tmp = new Real(y1);
			 				 Real dxtmp = new Real(dx);
			 				 Real dytmp = new Real(dy);
			 				 Real ddtmp = new Real(dd);
			 				 Real dctmp = new Real(dc);
			 				 Real ctmp = new Real(c);
			 				 Real ktmp = new Real(k);
			 				 Real istmp = new Real(is);
			 				 Real ittmp = new Real(it);
			                 g65(xtmp, ytmp, x1tmp, y1tmp, dxtmp, dytmp, ddtmp, dctmp, ctmp, ktmp, istmp, ittmp);
			                 x=xtmp.doubleValue();
							 y=ytmp.doubleValue();
							 x1=x1tmp.doubleValue();
							 y1=y1tmp.doubleValue();
							 dx=dxtmp.doubleValue();
							 dy=dytmp.doubleValue();
							 dd=ddtmp.doubleValue();
							 dc=dctmp.doubleValue();
							 c=ctmp.doubleValue();
							 k=ktmp.intValue(); 
							 is=istmp.intValue();
							 it=ittmp.intValue();
		                     if (it==0) 
		 						continue;
		
			 				 Real xtmp1 = new Real(x);
			 				 Real ytmp1 = new Real(y);
			 				 Real ptmp1 = new Real(p);
			 				 Real qtmp1 = new Real(q);
			 				 Real wtmp1 = new Real(w);
			 				 Real ktmp1 = new Real(k);
				 			 g90(xr,xi,dblCoef,xtmp1,ytmp1,ptmp1,qtmp1,wtmp1,ktmp1);
			                 x=xtmp1.doubleValue();
							 y=ytmp1.doubleValue();
							 p=ptmp1.doubleValue();
							 q=qtmp1.doubleValue();
							 w=wtmp1.doubleValue();
							 k=ktmp1.intValue(); 
		                 }
		                 else
		                 { 
		 					 dx=(u*u1+v*v1)/p;
		                     dy=(u1*v-v1*u)/p;
		                     t=1.0+4.0/k;
			 				 Real ttmp = new Real(t);
			 				 Real xtmp = new Real(x);
			 				 Real ytmp = new Real(y);
			 				 Real x1tmp = new Real(x1);
			 				 Real y1tmp = new Real(y1);
			 				 Real dxtmp = new Real(dx);
			 				 Real dytmp = new Real(dy);
			 				 Real ptmp = new Real(p);
			 				 Real qtmp = new Real(q);
			 				 Real ktmp = new Real(k);
			 				 Real ittmp = new Real(it);
			 				 g60(ttmp,xtmp,ytmp,x1tmp,y1tmp,dxtmp,dytmp,ptmp,qtmp,ktmp,ittmp);
			                 t=ttmp.doubleValue();
			                 x=xtmp.doubleValue();
							 y=ytmp.doubleValue();
							 x1=x1tmp.doubleValue();
							 y1=y1tmp.doubleValue();
							 dx=dxtmp.doubleValue();
							 dy=dytmp.doubleValue();
							 p=ptmp.doubleValue();
							 q=qtmp.doubleValue();
							 k=ktmp.intValue(); 
							 it=ittmp.intValue();
		                     if (t>=1.0e-03) 
		 						continue;
		
		                     if (g>1.0e-18)
		                     { 
		 						 it=0;
				 				 Real xtmp1 = new Real(x);
				 				 Real ytmp1 = new Real(y);
				 				 Real x1tmp1 = new Real(x1);
				 				 Real y1tmp1 = new Real(y1);
				 				 Real dxtmp1 = new Real(dx);
				 				 Real dytmp1 = new Real(dy);
				 				 Real ddtmp1 = new Real(dd);
				 				 Real dctmp1 = new Real(dc);
				 				 Real ctmp1 = new Real(c);
				 				 Real ktmp1 = new Real(k);
				 				 Real istmp1 = new Real(is);
				 				 Real ittmp1 = new Real(it);
				                 g65(xtmp1, ytmp1, x1tmp1, y1tmp1, dxtmp1, dytmp1, ddtmp1, dctmp1, ctmp1, ktmp1, istmp1, ittmp1);
				                 x=xtmp1.doubleValue();
								 y=ytmp1.doubleValue();
								 x1=x1tmp1.doubleValue();
								 y1=y1tmp1.doubleValue();
								 dx=dxtmp1.doubleValue();
								 dy=dytmp1.doubleValue();
								 dd=ddtmp1.doubleValue();
								 dc=dctmp1.doubleValue();
								 c=ctmp1.doubleValue();
								 k=ktmp1.intValue(); 
								 is=istmp1.intValue();
								 it=ittmp1.intValue();
		                         if (it==0) 
		 							continue;
		                     }
		                     
			 				 Real xtmp1 = new Real(x);
			 				 Real ytmp1 = new Real(y);
			 				 Real ptmp1 = new Real(p);
			 				 Real qtmp1 = new Real(q);
			 				 Real wtmp1 = new Real(w);
			 				 Real ktmp1 = new Real(k);
				 			 g90(xr,xi,dblCoef,xtmp1,ytmp1,ptmp1,qtmp1,wtmp1,ktmp1);
			                 x=xtmp1.doubleValue();
							 y=ytmp1.doubleValue();
							 p=ptmp1.doubleValue();
							 q=qtmp1.doubleValue();
							 w=wtmp1.doubleValue();
							 k=ktmp1.intValue(); 
		                 }
		            }
		            break;
		         }
	        }
	        
	 		if (k==1) 
	 			jt=0;
	        else 
	 			jt=1;
	     }
	     
	 	return true;
	 }
	
	
	 /**
	  * 内部函数
	  */
	 private void g60(Real t, Real x, Real y, Real x1, Real y1, 
	 		Real dx, Real dy, Real p, Real q, Real k, Real it)
	 { 
	 	 it.setValue(1);
	     while (it.intValue()==1)
	     { 
	 		t.setValue(t.doubleValue()/1.67); 
	 		it.setValue(0);
	        x1.setValue(x.doubleValue()-t.doubleValue()*dx.doubleValue());
	        y1.setValue(y.doubleValue()-t.doubleValue()*dy.doubleValue());
	         if (k.intValue()>=50)
	 		{ 
	 			p.setValue(Math.sqrt(x1.doubleValue()*x1.doubleValue()+y1.doubleValue()*y1.doubleValue()));
	            q.setValue(Math.exp(85.0/k.doubleValue()));
	            if (p.doubleValue()>=q.doubleValue()) 
	 				it.setValue(1);
	         }
	     }
	 }
	
	 /**
	  * 内部函数
	  */
	 private void g90(double[] xr, double[] xi, double[] dblCoef,
	 		Real x, Real y, Real p, Real q, Real w, Real k)
	 { 
	 	int i;
	     
	 	if (Math.abs(y.doubleValue())<=1.0e-06)
	 	{ 
	 		p.setValue(-x.doubleValue()); 
	 		y.setValue(0.0); 
	 		q.setValue(0.0);
	 	}
	    else
	    { 
	 		 p.setValue(-2.0*x.doubleValue()); 
	 		 q.setValue(x.doubleValue()*x.doubleValue() + y.doubleValue()*y.doubleValue());
	         xr[k.intValue()-1]=x.doubleValue()*w.doubleValue();
	         xi[k.intValue()-1]=-y.doubleValue()*w.doubleValue();
	         k.setValue(k.intValue()-1);
	    }
	     
	 	for (i=1; i<=k.intValue(); i++)
	    { 
	 		dblCoef[i]=dblCoef[i]-dblCoef[i-1]*p.doubleValue();
	        dblCoef[i+1]=dblCoef[i+1]-dblCoef[i-1]*q.doubleValue();
	    }
	     
	 	xr[k.intValue()-1]=x.doubleValue()*w.doubleValue(); 
	 	xi[k.intValue()-1]=y.doubleValue()*w.doubleValue();
        k.setValue(k.intValue()-1);
	    if (k.intValue()==1)
	    { 
	 		xr[0]=-dblCoef[1]*w.doubleValue()/dblCoef[0]; 
	 		xi[0]=0.0;
	 	}
	 }
	
	 /**
	  * 内部函数
	  */
	 private void g65(Real x,Real y,Real x1,Real y1,Real dx,Real dy,Real dd,Real dc,Real c,
	 		Real k,Real is,Real it)
	 { 
	 	if (it.intValue()==0)
	     { 
	 		 is.setValue(1);
	         dd.setValue(Math.sqrt(dx.doubleValue()*dx.doubleValue()+dy.doubleValue()*dy.doubleValue()));
	         if (dd.doubleValue()>1.0) 
	 			dd.setValue(1.0);
	         dc.setValue(6.28/(4.5*k.doubleValue())); 
	 		 c.setValue(0.0);
	     }
	     
	 	while(true)
	     { 
	 		 c.setValue(c.doubleValue()+dc.doubleValue());
	         dx.setValue(dd.doubleValue()*Math.cos(c.doubleValue())); 
	 		 dy.setValue(dd.doubleValue()*Math.sin(c.doubleValue()));
	         x1.setValue(x.doubleValue()+dx.doubleValue()); 
	         y1.setValue(y.doubleValue()+dy.doubleValue()); 
	         if (c.doubleValue()<=6.29)
	         { 
	 			it.setValue(0); 
	 			return;
	 		}
	         
	 		 dd.setValue(dd.doubleValue()/1.67);
	         if (dd.doubleValue()<=1.0e-07)
	         { 
	 			it.setValue(1); 
	 			return;
	 		}
	         
	 		c.setValue(0.0);
	     }
	 }
	
	 /**
	  * 求复系数代数方程全部根的牛顿下山法
	  * 
	  * @param n - 多项式方程的次数
	  * @param ar - 一维数组，长度为n+1，按降幂次序依次存放n次多项式方程的
	  *             n+1个系数的实部
	  * @param ai - 一维数组，长度为n+1，按降幂次序依次存放n次多项式方程的
	  *             n+1个系数的虚部
	  * @param xr - 一维数组，长度为n，返回n个根的实部
	  * @param xi - 一维数组，长度为n，返回n个根的虚部
	  * @return boolean 型，求解是否成功
	  */
	 public boolean getRootNewtonDownHill(int n, double[] ar, double[] ai, double[] xr, double[] xi)
	 { 
	 	 int m=0,i=0,jt=0,k=0,is=0,it=0;
	     double t=0,x=0,y=0,x1=0,y1=0,dx=0,dy=0,p=0,q=0,w=0,dd=0,dc=0,c=0;
	     double g=0,u=0,v=0,pq=0,g1=0,u1=0,v1=0;
	
	 	// 初始判断
	     m=n;
	     p=Math.sqrt(ar[m]*ar[m]+ai[m]*ai[m]);
	     while ((m>0)&&(p+1.0==1.0))
	     {  
	 		m=m-1;
	         p=Math.sqrt(ar[m]*ar[m]+ai[m]*ai[m]);
	     }
	     
	 	// 求解失败
	 	if (m<=0)
	 		return false;
	
	     for (i=0; i<=m; i++)
	     { 
	 		ar[i]=ar[i]/p; 
	 		ai[i]=ai[i]/p;
	 	}
	     
	 	for (i=0; i<=m/2; i++)
	     { 
	 		w=ar[i]; 
	 		ar[i]=ar[m-i]; 
	 		ar[m-i]=w;
	         w=ai[i]; 
	 		ai[i]=ai[m-i]; 
	 		ai[m-i]=w;
	     }
	     
	 	// 迭代求解
	 	k=m; 
	 	is=0; 
	 	w=1.0;
	     jt=1;
	     while (jt==1)
	     { 
	 		pq=Math.sqrt(ar[k]*ar[k]+ai[k]*ai[k]);
	 		while (pq<1.0e-12)
	         { 
	 			xr[k-1]=0.0; 
	 			xi[k-1]=0.0; 
	 			k=k-1;
	             if (k==1)
	             { 
	 				p=ar[0]*ar[0]+ai[0]*ai[0];
	                 xr[0]=-w*(ar[0]*ar[1]+ai[0]*ai[1])/p;
	                 xi[0]=w*(ar[1]*ai[0]-ar[0]*ai[1])/p;
	                 
	 				return true;
	             }
	             
	 			pq=Math.sqrt(ar[k]*ar[k]+ai[k]*ai[k]);
	         }
	 		
	 		q=Math.log(pq); 
	 		q=q/(1.0*k); 
	 		q=Math.exp(q);
	         p=q; 
	 		w=w*p;
	         for (i=1; i<=k; i++)
	         { 
	 			ar[i]=ar[i]/q; 
	 			ai[i]=ai[i]/q; 
	 			q=q*p;
	 		}
	         
	 		x=0.0001; 
	 		x1=x; 
	 		y=0.2; 
	 		y1=y; 
	 		dx=1.0;
	         g=1.0e+37; 
	 
	         while (true)
	         {
		         u=ar[0]; 
		 		v=ai[0];
		         for (i=1; i<=k; i++)
		         { 
		 			p=u*x1; 
		 			q=v*y1;
		             pq=(u+v)*(x1+y1);
		             u=p-q+ar[i]; 
		 			v=pq-p-q+ai[i];
		         }
		         
		 		g1=u*u+v*v;
		         if (g1>=g)
		         { 
		 			if (is!=0)
		             { 
		 				 it=1;
		 				 Real xtmp = new Real(x);
		 				 Real ytmp = new Real(y);
		 				 Real x1tmp = new Real(x1);
		 				 Real y1tmp = new Real(y1);
		 				 Real dxtmp = new Real(dx);
		 				 Real dytmp = new Real(dy);
		 				 Real ddtmp = new Real(dd);
		 				 Real dctmp = new Real(dc);
		 				 Real ctmp = new Real(c);
		 				 Real ktmp = new Real(k);
		 				 Real istmp = new Real(is);
		 				 Real ittmp = new Real(it);
		                 g65c(xtmp, ytmp, x1tmp, y1tmp, dxtmp, dytmp, ddtmp, dctmp, ctmp, ktmp, istmp, ittmp);
		                 x=xtmp.doubleValue();
						 y=ytmp.doubleValue();
						 x1=x1tmp.doubleValue();
						 y1=y1tmp.doubleValue();
						 dx=dxtmp.doubleValue();
						 dy=dytmp.doubleValue();
						 dd=ddtmp.doubleValue();
						 dc=dctmp.doubleValue();
						 c=ctmp.doubleValue();
						 k=ktmp.intValue(); 
						 is=istmp.intValue();
						 it=ittmp.intValue();
		                 if (it==0) 
		 					continue;
		             }
		             else
		             { 
		 				 Real ttmp = new Real(t);
		 				 Real xtmp = new Real(x);
		 				 Real ytmp = new Real(y);
		 				 Real x1tmp = new Real(x1);
		 				 Real y1tmp = new Real(y1);
		 				 Real dxtmp = new Real(dx);
		 				 Real dytmp = new Real(dy);
		 				 Real ptmp = new Real(p);
		 				 Real qtmp = new Real(q);
		 				 Real ktmp = new Real(k);
		 				 Real ittmp = new Real(it);
		 				 g60c(ttmp,xtmp,ytmp,x1tmp,y1tmp,dxtmp,dytmp,ptmp,qtmp,ktmp,ittmp);
		                 t=ttmp.doubleValue();
		                 x=xtmp.doubleValue();
						 y=ytmp.doubleValue();
						 x1=x1tmp.doubleValue();
						 y1=y1tmp.doubleValue();
						 dx=dxtmp.doubleValue();
						 dy=dytmp.doubleValue();
						 p=ptmp.doubleValue();
						 q=qtmp.doubleValue();
						 k=ktmp.intValue(); 
						 it=ittmp.intValue();
		                 if (t>=1.0e-03) 
		 					continue;
		                 
		 				if (g>1.0e-18)
		                 { 
		 					it=0;
			 				 Real xtmp1 = new Real(x);
			 				 Real ytmp1 = new Real(y);
			 				 Real x1tmp1 = new Real(x1);
			 				 Real y1tmp1 = new Real(y1);
			 				 Real dxtmp1 = new Real(dx);
			 				 Real dytmp1 = new Real(dy);
			 				 Real ddtmp1 = new Real(dd);
			 				 Real dctmp1 = new Real(dc);
			 				 Real ctmp1 = new Real(c);
			 				 Real ktmp1 = new Real(k);
			 				 Real istmp1 = new Real(is);
			 				 Real ittmp1 = new Real(it);
			                 g65c(xtmp1, ytmp1, x1tmp1, y1tmp1, dxtmp1, dytmp1, ddtmp1, dctmp1, ctmp1, ktmp1, istmp1, ittmp1);
			                 x=xtmp1.doubleValue();
							 y=ytmp1.doubleValue();
							 x1=x1tmp1.doubleValue();
							 y1=y1tmp1.doubleValue();
							 dx=dxtmp1.doubleValue();
							 dy=dytmp1.doubleValue();
							 dd=ddtmp1.doubleValue();
							 dc=dctmp1.doubleValue();
							 c=ctmp1.doubleValue();
							 k=ktmp1.intValue(); 
							 is=istmp1.intValue();
							 it=ittmp1.intValue();
		                     if (it==0) 
		 						continue;
		                 }
		             }
		             
	 				 Real xtmp = new Real(x);
	 				 Real ytmp = new Real(y);
	 				 Real ptmp = new Real(p);
	 				 Real wtmp = new Real(w);
	 				 Real ktmp = new Real(k);
		 			 g90c(xr,xi,ar,ai,xtmp,ytmp,ptmp,wtmp,ktmp);
	                 x=xtmp.doubleValue();
					 y=ytmp.doubleValue();
					 p=ptmp.doubleValue();
					 w=wtmp.doubleValue();
					 k=ktmp.intValue(); 
		 			break;
		         }
		         else
		         { 
		 			g=g1; 
		 			x=x1; 
		 			y=y1; 
		 			is=0;
		             if (g<=1.0e-22)
		             {
		 				 Real xtmp = new Real(x);
		 				 Real ytmp = new Real(y);
		 				 Real ptmp = new Real(p);
		 				 Real wtmp = new Real(w);
		 				 Real ktmp = new Real(k);
			 			 g90c(xr,xi,ar,ai,xtmp,ytmp,ptmp,wtmp,ktmp);
		                 x=xtmp.doubleValue();
						 y=ytmp.doubleValue();
						 p=ptmp.doubleValue();
						 w=wtmp.doubleValue();
						 k=ktmp.intValue(); 
		             }
		             else
		             { 
		 				u1=k*ar[0]; 
		 				v1=ai[0];
		                 for (i=2; i<=k; i++)
		                 { 
		 					p=u1*x; 
		 					q=v1*y; 
		 					pq=(u1+v1)*(x+y);
		                     u1=p-q+(k-i+1)*ar[i-1];
		                     v1=pq-p-q+(k-i+1)*ai[i-1];
		                 }
		                 
		 				p=u1*u1+v1*v1;
		                 if (p<=1.0e-20)
		                 { 
		 					it=0;
			 				 Real xtmp = new Real(x);
			 				 Real ytmp = new Real(y);
			 				 Real x1tmp = new Real(x1);
			 				 Real y1tmp = new Real(y1);
			 				 Real dxtmp = new Real(dx);
			 				 Real dytmp = new Real(dy);
			 				 Real ddtmp = new Real(dd);
			 				 Real dctmp = new Real(dc);
			 				 Real ctmp = new Real(c);
			 				 Real ktmp = new Real(k);
			 				 Real istmp = new Real(is);
			 				 Real ittmp = new Real(it);
			                 g65c(xtmp, ytmp, x1tmp, y1tmp, dxtmp, dytmp, ddtmp, dctmp, ctmp, ktmp, istmp, ittmp);
			                 x=xtmp.doubleValue();
							 y=ytmp.doubleValue();
							 x1=x1tmp.doubleValue();
							 y1=y1tmp.doubleValue();
							 dx=dxtmp.doubleValue();
							 dy=dytmp.doubleValue();
							 dd=ddtmp.doubleValue();
							 dc=dctmp.doubleValue();
							 c=ctmp.doubleValue();
							 k=ktmp.intValue(); 
							 is=istmp.intValue();
							 it=ittmp.intValue();
		                     if (it==0) 
		 						continue;
		                     
			 				 Real xtmp1 = new Real(x);
			 				 Real ytmp1 = new Real(y);
			 				 Real ptmp1 = new Real(p);
			 				 Real wtmp1 = new Real(w);
			 				 Real ktmp1 = new Real(k);
				 			 g90c(xr,xi,ar,ai,xtmp1,ytmp1,ptmp1,wtmp1,ktmp1);
			                 x=xtmp1.doubleValue();
							 y=ytmp1.doubleValue();
							 p=ptmp1.doubleValue();
							 w=wtmp1.doubleValue();
							 k=ktmp1.intValue(); 
		                 }
		                 else
		                 { 
		 					dx=(u*u1+v*v1)/p;
		                     dy=(u1*v-v1*u)/p;
		                     t=1.0+4.0/k;
			 				 Real ttmp = new Real(t);
			 				 Real xtmp = new Real(x);
			 				 Real ytmp = new Real(y);
			 				 Real x1tmp = new Real(x1);
			 				 Real y1tmp = new Real(y1);
			 				 Real dxtmp = new Real(dx);
			 				 Real dytmp = new Real(dy);
			 				 Real ptmp = new Real(p);
			 				 Real qtmp = new Real(q);
			 				 Real ktmp = new Real(k);
			 				 Real ittmp = new Real(it);
			 				 g60c(ttmp,xtmp,ytmp,x1tmp,y1tmp,dxtmp,dytmp,ptmp,qtmp,ktmp,ittmp);
			                 t=ttmp.doubleValue();
			                 x=xtmp.doubleValue();
							 y=ytmp.doubleValue();
							 x1=x1tmp.doubleValue();
							 y1=y1tmp.doubleValue();
							 dx=dxtmp.doubleValue();
							 dy=dytmp.doubleValue();
							 p=ptmp.doubleValue();
							 q=qtmp.doubleValue();
							 k=ktmp.intValue(); 
							 it=ittmp.intValue();
		                     if (t>=1.0e-03) 
		 						continue;
		                     
		 					if (g>1.0e-18)
		                     { 
		 						it=0;
				 				 Real xtmp1 = new Real(x);
				 				 Real ytmp1 = new Real(y);
				 				 Real x1tmp1 = new Real(x1);
				 				 Real y1tmp1 = new Real(y1);
				 				 Real dxtmp1 = new Real(dx);
				 				 Real dytmp1 = new Real(dy);
				 				 Real ddtmp1 = new Real(dd);
				 				 Real dctmp1 = new Real(dc);
				 				 Real ctmp1 = new Real(c);
				 				 Real ktmp1 = new Real(k);
				 				 Real istmp1 = new Real(is);
				 				 Real ittmp1 = new Real(it);
				                 g65c(xtmp1, ytmp1, x1tmp1, y1tmp1, dxtmp1, dytmp1, ddtmp1, dctmp1, ctmp1, ktmp1, istmp1, ittmp1);
				                 x=xtmp1.doubleValue();
								 y=ytmp1.doubleValue();
								 x1=x1tmp1.doubleValue();
								 y1=y1tmp1.doubleValue();
								 dx=dxtmp1.doubleValue();
								 dy=dytmp1.doubleValue();
								 dd=ddtmp1.doubleValue();
								 dc=dctmp1.doubleValue();
								 c=ctmp1.doubleValue();
								 k=ktmp1.intValue(); 
								 is=istmp1.intValue();
								 it=ittmp1.intValue();
		                         if (it==0) 
		 							continue;
		                     }
		                     
			 				 Real xtmp1 = new Real(x);
			 				 Real ytmp1 = new Real(y);
			 				 Real ptmp1 = new Real(p);
			 				 Real wtmp1 = new Real(w);
			 				 Real ktmp1 = new Real(k);
				 			 g90c(xr,xi,ar,ai,xtmp1,ytmp1,ptmp1,wtmp1,ktmp1);
			                 x=xtmp1.doubleValue();
							 y=ytmp1.doubleValue();
							 p=ptmp1.doubleValue();
							 w=wtmp1.doubleValue();
							 k=ktmp1.intValue(); 
		                 }
		             }
		             break;
		         }
	         }
	         
	 		if (k==1) 
	 			jt=0;
	         else 
	 			jt=1;
	     }
	     
	 	return true;
	 }
	
	 /**
	  * 内部函数
	  */
	 private void g60c(Real t,Real x,Real y,Real x1,Real y1,Real dx,Real dy,Real p,
	 		Real q,Real k,Real it)
	 { 
	 	 it.setValue(1);
	     while (it.intValue()==1)
	     { 
	 		 t.setValue(t.doubleValue()/1.67); 
	 		 it.setValue(0);
	         x1.setValue(x.doubleValue()-t.doubleValue()*dx.doubleValue());
	         y1.setValue(y.doubleValue()-t.doubleValue()*dy.doubleValue());
	         if (k.intValue()>=30)
	 		{ 
	 			 p.setValue(Math.sqrt(x1.doubleValue()*x1.doubleValue()+y1.doubleValue()*y1.doubleValue()));
	             q.setValue(Math.exp(75.0/k.doubleValue()));
	             if (p.doubleValue()>=q.doubleValue()) 
	 				it.setValue(1);
	         }
	     }
	 }
	
	 /**
	  * 内部函数
	  */
	 private void g90c(double[] xr,double[] xi,double[] ar,double[] ai,Real x,Real y,Real p,Real w,Real k)
	 { 
	 	 int i;
	     for (i=1; i<=k.intValue(); i++)
	     { 
	 		 ar[i]=ar[i]+ar[i-1]*x.doubleValue()-ai[i-1]*y.doubleValue();
	         ai[i]=ai[i]+ar[i-1]*y.doubleValue()+ai[i-1]*x.doubleValue();
	     }
	     
	 	 xr[k.intValue()-1]=x.doubleValue()*w.doubleValue(); 
	 	 xi[k.intValue()-1]=y.doubleValue()*w.doubleValue();
	     k.setValue(k.intValue()-1);
	     if (k.intValue()==1)
	     { 
	 		 p.setValue(ar[0]*ar[0]+ai[0]*ai[0]);
	         xr[0]=-w.doubleValue()*(ar[0]*ar[1]+ai[0]*ai[1])/p.doubleValue();
	         xi[0]=w.doubleValue()*(ar[1]*ai[0]-ar[0]*ai[1])/p.doubleValue();
	     }
	 }
	
	 /**
	  * 内部函数
	  */
	 private void g65c(Real x,Real y,Real x1,Real y1,Real dx,Real dy,Real dd,Real dc,Real c,Real k,Real is,Real it)
	 { 
	 	if (it.intValue()==0)
	     { 
	 		 is.setValue(1);
	         dd.setValue(Math.sqrt(dx.doubleValue()*dx.doubleValue()+dy.doubleValue()*dy.doubleValue()));
	         if (dd.doubleValue()>1.0) 
	 			dd.setValue(1.0);
	         dc.setValue(6.28/(4.5*k.doubleValue())); 
	 		 c.setValue(0.0);
	     }
	     
	 	while(true)
	     { 
	 		 c.setValue(c.doubleValue()+dc.doubleValue());
	         dx.setValue(dd.doubleValue()*Math.cos(c.doubleValue())); 
	         dy.setValue(dd.doubleValue()*Math.sin(c.doubleValue())); 
	         x1.setValue(x.doubleValue()+dx.doubleValue()); 
	 		 y1.setValue(y.doubleValue()+dy.doubleValue());
	         if (c.doubleValue()<=6.29)
	         { 
	 			it.setValue(0); 
	 			return;
	 		}
	         
	 		 dd.setValue(dd.doubleValue()/1.67);
	         if (dd.intValue()<=1.0e-07)
	         { 
	 			it.setValue(1); 
	 			return;
	 		}
	         
	 		c.setValue(0.0);
	     }
	 }
	
	 /**
	  * 求非线性方程一个实根的蒙特卡洛法
	  * 
	  * 调用时，须覆盖计算方程左端函数f(x)值的虚函数double func(double x)
	  * 
	  * @param x - 传入初值（猜测解），返回求得的实根
	  * @param xStart - 均匀分布的端点初值
	  * @param nControlB - 控制参数
	  * @param eps - 控制精度
	  */
	 public void getRootMonteCarlo(Real x, double xStart, int nControlB, double eps)
	 { 
	 	int k;
	 	double xx,a,y,x1,y1,r;
	     
	 	// 求解条件
	 	a = xStart; 
	 	k = 1; 
	 	r = 1.0;
	
	 	// 初值
	 	xx = x.doubleValue(); 
	 	y = func(xx);
	
	 	// 精度控制求解
	     while (a>=eps)
	     { 
	     	Real rtmp = new Real(r);
	 		x1=rnd(rtmp);
	 		r=rtmp.doubleValue();
	
	 		x1=-a+2.0*a*x1;
	         x1=xx+x1; 
	 		y1=func(x1);
	         
	 		k=k+1;
	         if (Math.abs(y1)>=Math.abs(y))
	         { 
	 			if (k>nControlB) 
	 			{ 
	 				k=1; 
	 				a=a/2.0; 
	 			}
	 		}
	         else
	         { 
	 			k=1; 
	 			xx=x1; 
	 			y=y1;
	             if (Math.abs(y)<eps)
	             { 
	 				x.setValue(xx); 
	 				return; 
	 			}
	         }
	     }
	     
		x.setValue(xx); 
	 }
	
	 /**
	  * 内部函数
	  */
	 private double rnd(Real r)
	 {
	 	int m;
	    double s,u,v,p;
	     
	 	s=65536.0; 
	 	u=2053.0; 
	 	v=13849.0;
	    m=(int)(r.doubleValue()/s); 
	 	r.setValue(r.doubleValue()-m*s);
	    r.setValue(u*r.doubleValue()+v); 
	 	m=(int)(r.doubleValue()/s);
	 	r.setValue(r.doubleValue()-m*s); 
	 	p=r.doubleValue()/s;
	     
	 	return(p);
	 }
	
	 /**
	  * 求实函数或复函数方程一个复根的蒙特卡洛法
	  * 
	  * 调用时，须覆盖计算方程左端函数的模值||f(x, y)||的虚函数
	  *          double func(double x, double y)
	  * 
	  * @param x - 传入初值（猜测解）的实部，返回求得的根的实部
	  * @param y - 传入初值（猜测解）的虚部，返回求得的根的虚部
	  * @param xStart - 均匀分布的端点初值
	  * @param nControlB - 控制参数
	  * @param eps - 控制精度
	  */
	 public void getRootMonteCarlo(Real x, Real y, double xStart, int nControlB, double eps)
	 { 
	     int k;
	     double xx,yy,a,r,z,x1,y1,z1;
	
	 	// 求解条件与初值
	     a=xStart; 
	 	k=1; 
	 	r=1.0; 
	 	xx=x.doubleValue(); 
	 	yy=y.doubleValue();
	     z=func(xx,yy);
	     
	 	// 精度控制求解
	 	while (a>=eps)
	    { 
	     	Real rtmp = new Real(r);
	 		x1=-a+2.0*a*rnd(rtmp); 
	 		r=rtmp.doubleValue();

	 		x1=xx+x1; 
	 		
	     	Real rtmp1 = new Real(r);
	        y1=-a+2.0*a*rnd(rtmp1);
	 		r=rtmp1.doubleValue();
	        
	 		y1=yy+y1;
	         
	 		z1=func(x1,y1);
	         
	 		k=k+1;
	         if (z1>=z)
	         { 
	 			if (k>nControlB) 
	 			{ 
	 				k=1; 
	 				a=a/2.0; 
	 			}
	 		}
	         else
	         { 
	 			k=1; 
	 			xx=x1; 
	 			yy=y1;  
	 			z=z1;
	             if (z<eps)
	             { 
	 				x.setValue(xx); 
	 				y.setValue(yy); 
	 				return; 
	 			}
	         }
	     }
	     
		x.setValue(xx); 
		y.setValue(yy); 
	 }
	
	 /**
	  * 求非线性方程组一组实根的梯度法
	  * 
	  * 调用时，须覆盖计算方程左端函数f(x)值及其偏导数值的虚函数
	  *          double func(double[] x, double[] y)
	  * 
	  * @param n - 方程的个数，也是未知数的个数
	  * @param x - 一维数组，长度为n，存放一组初值x0, x1, …, xn-1，
	  *            返回时存放方程组的一组实根
	  * @param nMaxIt - 迭代次数
	  * @param eps - 控制精度
	  * @return boolean 型，求解是否成功
	  */
	 public boolean getRootsetGrad(int n, double[] x, int nMaxIt, double eps)
	 { 
	     int l,j;
	     double f,d,s;
	     double[] y = new double[n];
	
	     l=nMaxIt;
	     f=func(x,y);
	
	 	// 控制精度，迭代求解
	     while (f>=eps)
	     { 
	 		l=l-1;
	        if (l==0) 
	 		{ 
	 			return true;
	 		}
	         
	 		d=0.0;
	        for (j=0; j<=n-1; j++) 
	 			d=d+y[j]*y[j];
	        if (d+1.0==1.0) 
	 		{ 
	 			return false;
	 		}
	         
	 		s=f/d;
	        for (j=0; j<=n-1; j++) 
	 			x[j]=x[j]-s*y[j];
	         
	 		f=func(x,y);
	     }
	     
	 	// 是否在有效迭代次数内达到精度
	 	return (nMaxIt>l);
	 }
	
	 /**
	  * 求非线性方程组一组实根的拟牛顿法
	  * 
	  * 调用时，须覆盖计算方程左端函数f(x)值及其偏导数值的虚函数
	  *          double func(double[] x, double[] y)
	  * 
	  * @param n - 方程的个数，也是未知数的个数
	  * @param x - 一维数组，长度为n，存放一组初值x0, x1, …, xn-1，
	  *            返回时存放方程组的一组实根
	  * @param t - 控制h大小的变量，0<t<1
	  * @param h - 增量初值
	  * @param nMaxIt - 迭代次数
	  * @param eps - 控制精度
	  * @return boolean 型，求解是否成功
	  */
	 public boolean getRootsetNewton(int n, double[] x, double t, double h, int nMaxIt, double eps)
	 { 
	     int i,j,l;
	     double am,z,beta,d;
	
	     double[] y = new double[n];
	 	
	 	 // 构造矩阵
	 	 Matrix mtxCoef = new Matrix(n, n);
	 	 Matrix mtxConst = new Matrix(n, 1);
	     double[] a = mtxCoef.getData();
	     double[] b = mtxConst.getData();
	
	 	 // 迭代求解
	     l=nMaxIt; 
	 	 am=1.0+eps;
	     while (am>=eps)
	     { 
	 		func(x,b);
	         
	 		am=0.0;
	         for (i=0; i<=n-1; i++)
	         { 
	 			z=Math.abs(b[i]);
	             if (z>am) 
	 				am=z;
	         }
	         
	 		if (am>=eps)
	         { 
	 			l=l-1;
	             if (l==0)
	             { 
	                 return false;
	             }
	             
	 			for (j=0; j<=n-1; j++)
	             { 
	 				z=x[j]; 
	 				x[j]=x[j]+h;
	                 
	 				func(x,y);
	                 
	 				for (i=0; i<=n-1; i++) 
	 					a[i*n+j]=y[i];
	                 
	 				x[j]=z;
	             }
	             
	 			// 调用全选主元高斯消元法
	 			LEquations leqs = new LEquations(mtxCoef, mtxConst);
	 			Matrix mtxResult = new Matrix();
	 			if (! leqs.getRootsetGauss(mtxResult))
	 			{
	 				return false;
	 			}
	
	 			mtxConst.setValue(mtxResult);
	 			b = mtxConst.getData();
	
	 			beta=1.0;
	             for (i=0; i<=n-1; i++) 
	 				beta=beta-b[i];
	             
	 			if (beta == 0.0)
	             { 
	                 return false;
	             }
	             
	 			d=h/beta;
	             for (i=0; i<=n-1; i++) 
	 				x[i]=x[i]-d*b[i];
	             
	 			h=t*h;
	         }
	     }
	     
	 	// 是否在有效迭代次数内达到精度
	 	return (nMaxIt>l);
	 }
	
	 /**
	  * 求非线性方程组最小二乘解的广义逆法
	  * 
	  * 调用时，1. 须覆盖计算方程左端函数f(x)值及其偏导数值的虚函数
	  *              double func(double[] x, double[] y)
	  *        2. 须覆盖计算雅可比矩阵函数的虚函数
	  *              double FuncMJ(double[] x, double[] y)
	  * 
	  * @param m - 方程的个数
	  * @param n - 未知数的个数
	  * @param x - 一维数组，长度为n，存放一组初值x0, x1, …, xn-1，要求不全为0，
	  * 			返回时存放方程组的最小二乘解，当m=n时，即是非线性方程组的解
	  * @param eps1 - 最小二乘解的精度控制精度
	  * @param eps2 - 奇异值分解的精度控制精度
	  * @return boolean 型，求解是否成功
	  */
	 public boolean getRootsetGinv(int m, int n, double[] x, double eps1, double eps2)
	 { 
	 	int i,j,k,l,kk,jt;
        double alpha,z=0,h2,y1,y2,y3,y0,h1;
        double[] p,d,dx;
	     
	 	double[] y = new double[10];
	 	double[] b = new double[10];
	     
	 	// 控制参数
	 	int ka = Math.max(m, n)+1;
	 	double[] w = new double[ka];
	     
	 	// 设定迭代次数为60，迭代求解
	 	l=60; 
	 	alpha=1.0;
	     while (l>0)
	     { 
	 		Matrix mtxP = new Matrix(m, n);
	 		Matrix mtxD = new Matrix(m, 1);
	 		p = mtxP.getData();
	 		d = mtxD.getData();
	
	 		func(x,d);
	        funcMJ(x,p);
	
	 		// 构造线性方程组
	 		LEquations leqs = new LEquations(mtxP, mtxD);
	 		// 临时矩阵
	 		Matrix mtxAP = new Matrix();
	 		Matrix mtxU = new Matrix();
	 		Matrix mtxV = new Matrix();
	 		// 解矩阵
	 		Matrix mtxDX = new Matrix();
	 		// 基于广义逆的最小二乘解
	 		if (! leqs.getRootsetGinv(mtxDX, mtxAP, mtxU, mtxV, eps2))
	 		{ 
	 			return false;
	        }
	         
	 		dx = mtxDX.getData();
	
	 		j=0; 
	 		jt=1; 
	 		h2=0.0;
	        while (jt==1)
	        { 
	 			jt=0;
	             if (j<=2) 
	 				z=alpha+0.01*j;
	             else 
	 				z=h2;
	             
	 			for (i=0; i<=n-1; i++) 
	 				w[i]=x[i]-z*dx[i];
	             
	 			func(w,d);
	             
	 			y1=0.0;
	             for (i=0; i<=m-1; i++) 
	 				y1=y1+d[i]*d[i];
	             for (i=0; i<=n-1; i++)
	 				w[i]=x[i]-(z+0.00001)*dx[i];
	             
	 			func(w,d);
	             
	 			y2=0.0;
	             for (i=0; i<=m-1; i++) 
	 				y2=y2+d[i]*d[i];
	             
	 			y0=(y2-y1)/0.00001;
	             
	 			if (Math.abs(y0)>1.0e-10)
	             { 
	 				h1=y0; h2=z;
	                 if (j==0) 
	 				{ 
	 					y[0]=h1; 
	 					b[0]=h2;
	 				}
	                 else
	                 { 
	 					y[j]=h1; 
	 					kk=0; 
	 					k=0;
	                     while ((kk==0)&&(k<=j-1))
	                     { 
	 						y3=h2-b[k];
	                         if (Math.abs(y3)+1.0==1.0) 
	 							kk=1;
	                         else 
	 							h2=(h1-y[k])/y3;
	                         
	 						k=k+1;
	                     }
	                     
	 					b[j]=h2;
	                     if (kk!=0) 
	 						b[j]=1.0e+35;
	                     
	 					h2=0.0;
	                     for (k=j-1; k>=0; k--)
	 						h2=-y[k]/(b[k+1]+h2);
	                     
	 					h2=h2+b[0];
	                 }
	                 
	 				j=j+1;
	                 if (j<=7) 
	 					jt=1;
	                 else 
	 					z=h2;
	             }
	         }
	         
	 		alpha=z; 
	 		y1=0.0; 
	 		y2=0.0;
	         for (i=0; i<=n-1; i++)
	         { 
	 			dx[i]=-alpha*dx[i];
	             x[i]=x[i]+dx[i];
	             y1=y1+Math.abs(dx[i]);
	             y2=y2+Math.abs(x[i]);
	         }
	         
	 		// 求解成功
	 		if (y1<eps1*y2)
	         { 
	 			return true;
	         }
	         
	 		l=l-1;
	     }
	     
	 	// 求解失败
	 	return false;
	 }
	
	 /**
	  * 求非线性方程组一组实根的蒙特卡洛法
	  * 
	  * 调用时，须覆盖计算方程左端模函数值||F||的虚函数
	  *          double func(int n, double[] x)
	  * 其返回值为Sqr(f1*f1 + f2*f2 + … + fn*fn)
	  * 
	  * @param n - 方程的个数，也是未知数的个数
	  * @param x - 一维数组，长度为n，存放一组初值x0, x1, …, xn-1，
	  *            返回时存放方程组的一组实根
	  * @param xStart - 均匀分布的端点初值
	  * @param nControlB - 控制参数
	  * @param eps - 控制精度
	  */
	 public void getRootsetMonteCarlo(int n, double[] x, double xStart, int nControlB, double eps)
	 { 
	 	 int k,i;
	     double a,r,z,z1;
	
	     double[] y = new double[n];
	     
	 	// 初值
	 	a=xStart; 
	 	k=1; 
	 	r=1.0; 
	
	 	z = func(x);
	
	 	// 用精度控制迭代求解
	     while (a>=eps)
	     { 
	 		for (i=0; i<=n-1; i++)
	 		{
		     	Real rtmp = new Real(r);
	 			y[i]=-a+2.0*a*rnd(rtmp)+x[i];
		 		r=rtmp.doubleValue();
	 		}
	 		z1=func(y);
	         
	 		k=k+1;
	         if (z1>=z)
	         { 
	 			if (k>nControlB) 
	 			{ 
	 				k=1; 
	 				a=a/2.0; 
	 			}
	 		}
	         else
	         { 
	 			k=1; 
	             for (i=0; i<=n-1; i++) 
	 				x[i]=y[i];
	             
	 			// 求解成功
	 			z=z1;
	             if (z<eps)  
	 			{
	 				return;
	 			}
	         }
	     }
	 }
}
