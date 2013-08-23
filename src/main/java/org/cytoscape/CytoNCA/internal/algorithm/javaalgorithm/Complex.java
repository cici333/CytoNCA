/*
 * 操作复数的类Complex
 *
 * 周长发编制
 */
package org.cytoscape.CytoNCA.internal.algorithm.javaalgorithm;

/**
 * 操作复数的类Complex

 * @author 周长发
 * @version 1.0
 */
public class Complex 
{
	private double real = 0.0;			// 复数的实部
	private double imaginary = 0.0;		// 复数的虚部
	private double eps = 0.0;           // 缺省精度

	/**
	 * 基本构造函数
	 */
	public Complex() 
	{
	}

	/**
	 * 指定值构造函数
	 * 
	 * @param dblX - 指定的实部
	 * @param dblY - 指定的虚部
	 */
	public Complex(double dblX, double dblY)
	{
		real = dblX;
		imaginary = dblY;
	}

	/**
	 * 拷贝构造函数
	 * 
	 * @param other - 源复数
	 */
	public Complex(Complex other)
	{
		real = other.real;
		imaginary = other.imaginary;
	}

	/**
	 * 根据"a,b"形式的字符串来构造复数，以a为复数的实部，b为复数的虚部
	 * 
	 * @param s - "a,b"形式的字符串，a为复数的实部，b为复数的虚部
	 * @param sDelim - a, b之间的分隔符
	 */
	public Complex(String s, String sDelim)
	{
		setValue(s, sDelim);
	}

	/**
	 * 设置复数运算的精度
	 * 
	 * @param newEps - 新的精度值
	 */
	public void setEps(double newEps)
	{
		eps = newEps;
	}
	
	/**
	 * 取复数的精度值
	 * 
	 * @return double型，复数的精度值
	 */
	public double getEps()
	{
		return eps;
	}

	/**
	 * 指定复数的实部
	 * 
	 * @param dblX - 复数的实部
	 */
	public void setReal(double dblX)
	{
		real = dblX;
	}

	/**
	 * 指定复数的虚部
	 * 
	 * @param dblY - 复数的虚部
	 */
	public void setImag(double dblY)
	{
		imaginary = dblY;
	}

	/**
	 * 取复数的实部
	 * 
	 * @return double 型，复数的实部
	 */
	public double getReal()
	{
		return real;
	}

	/**
	 * 取复数的虚部
	 * 
	 * @return double 型，复数的虚部
	 */
	public double getImag()
	{
		return imaginary;
	}

	/**
	 * 指定复数的实部和虚部值
	 * 
	 * @param real - 指定的实部
	 * @param imag - 指定的虚部
	 */
	public void setValue(double real, double imag)
	{
		setReal(real);
		setImag(imag);
	}
	
	/**
	 * 将"a,b"形式的字符串转化为复数，以a为复数的实部，b为复数的虚部
	 * 
	 * @param s - "a,b"形式的字符串，a为复数的实部，b为复数的虚部
	 * @param sDelim - a, b之间的分隔符
	 */
	public void setValue(String s, String sDelim)
	{
		int nPos = s.indexOf(sDelim);
		if (nPos == -1)
		{
			s = s.trim();
			real = Double.parseDouble(s);
			imaginary = 0;
		}
		else
		{
			int nLen = s.length();
			String sLeft = s.substring(0, nPos);
			String sRight = s.substring(nPos+1, nLen);
			sLeft = sLeft.trim();
			sRight = sRight.trim();
			real = Double.parseDouble(sLeft);
			imaginary = Double.parseDouble(sRight);
		}
	}

	/**
	 * 将复数转化为"a+bj"形式的字符串
	 * 
	 * @return String 型，"a+bj"形式的字符串
	 */
	public String toString()
	{
		String s;
		if (real != 0.0)
		{
			if (imaginary > 0)
				s = new Float(real).toString() + "+" + new Float(imaginary).toString() + "j";
			else if (imaginary < 0)
				s = new Float(real).toString() + "-" + new Float(-1*imaginary).toString() + "j";
			else
				s = new Float(real).toString();
		}
		else
		{
			if (imaginary > 0)
				s = new Float(imaginary).toString() + "j";
			else if (imaginary < 0)
				s = new Float(-1*imaginary).toString() + "j";
			else
				s = new Float(real).toString();
		}

		return s;
	}

	/**
	 * 比较两个复数是否相等
	 * 
	 * @param cpxX - 用于比较的复数
	 * @return boolean型，相等则为true，否则为false
	 */
	public boolean equal(Complex cpxX)
	{
		return Math.abs(real - cpxX.real) <= eps && 
			Math.abs(imaginary - cpxX.imaginary) <= eps; 
	}

	/**
	 * 给复数赋值
	 * 
	 * @param cpxX - 用于给复数赋值的源复数
	 * @return Complex型，与cpxX相等的复数
	 */
	public Complex setValue(Complex cpxX)
	{
		real = cpxX.real;
		imaginary = cpxX.imaginary;

		return this;
	}

	/**
	 * 实现复数的加法
	 * 
	 * @param cpxX - 与指定复数相加的复数
	 * @return Complex型，指定复数与cpxX相加之和
	 */
	public Complex add(Complex cpxX)
	{
		double x = real + cpxX.real;
		double y = imaginary + cpxX.imaginary;

		return new Complex(x, y);
	}

	/**
	 * 实现复数的减法
	 * 
	 * @param cpxX - 与指定复数相减的复数
	 * @return Complex型，指定复数减去cpxX之差
	 */
	public Complex subtract(Complex cpxX)
	{
		double x = real - cpxX.real;
		double y = imaginary - cpxX.imaginary;

		return new Complex(x, y);
	}

	/**
	 * 实现复数的乘法
	 * 
	 * @param cpxX - 与指定复数相乘的复数
	 * @return Complex型，指定复数与cpxX相乘之积
	 */
	public Complex multiply(Complex cpxX)
	{
	    double x = real * cpxX.real - imaginary * cpxX.imaginary;
	    double y = real * cpxX.imaginary + imaginary * cpxX.real;

		return new Complex(x, y);
	}

	/**
	 * 实现复数的除法
	 * 
	 * @param cpxX - 与指定复数相除的复数
	 * @return Complex型，指定复数除与cpxX之商
	 */
	public Complex divide(Complex cpxX)
	{
	    double e, f, x, y;
	    
	    if (Math.abs(cpxX.real) >= Math.abs(cpxX.imaginary))
		{
	        e = cpxX.imaginary / cpxX.real;
	        f = cpxX.real + e * cpxX.imaginary;
	        
	        x = (real + imaginary * e) / f;
	        y = (imaginary - real * e) / f;
		}
	    else
	    {
			e = cpxX.real / cpxX.imaginary;
	        f = cpxX.imaginary + e * cpxX.real;
	        
	        x = (real * e + imaginary) / f;
	        y = (imaginary * e - real) / f;
	    }

		return new Complex(x, y);
	}

	/**
	 * 计算复数的模
	 * 
	 * @return double型，指定复数的模
	 */
	public double abs()
	{
	    // 求取实部和虚部的绝对值
	    double x = Math.abs(real);
	    double y = Math.abs(imaginary);

	    if (real == 0)
			return y;
	    if (imaginary == 0)
			return x;
	    
	    
	    // 计算模
	    if (x > y)
	        return (x * Math.sqrt(1 + (y / x) * (y / x)));
	    
	    return (y * Math.sqrt(1 + (x / y) * (x / y)));
	}

	/**
	 * 计算复数的根
	 * 
	 * @param n - 待求根的根次
	 * @param cpxR - Complex型数组，长度为n，返回复数的所有根
	 */
	public void root(int n, Complex[] cpxR)
	{
		if (n<1) 
			return;
	    
		double q = Math.atan2(imaginary, real);
	    double r = Math.sqrt(real*real + imaginary*imaginary);
	    if (r != 0)
	    { 
			r = (1.0/n)*Math.log(r);
			r = Math.exp(r);
		}

	    for (int k=0; k<=n-1; k++)
	    { 
			double t = (2.0*k*3.1415926+q)/n;
	        cpxR[k] = new Complex(r*Math.cos(t), r*Math.sin(t));
	    }
	}

	/**
	 * 计算复数的实幂指数
	 * 
	 * @param dblW - 待求实幂指数的幂次
	 * @return Complex型，复数的实幂指数值
	 */
	public Complex pow(double dblW)
	{
		// 常量
		final double PI = 3.14159265358979;

		// 局部变量
		double r, t;
	    
	    // 特殊值处理
	    if ((real == 0) && (imaginary == 0))
			return new Complex(0, 0);
	    
	    // 幂运算公式中的三角函数运算
	    if (real == 0)
		{
	        if (imaginary > 0)
	            t = 1.5707963268;
	        else
	            t = -1.5707963268;
		}
	    else
		{
	        if (real > 0)
	            t = Math.atan2(imaginary, real);
	        else
	        {
				if (imaginary >= 0)
	                t = Math.atan2(imaginary, real) + PI;
	            else
	                t = Math.atan2(imaginary, real) - PI;
			}
	    }
	    
	    // 模的幂
	    r = Math.exp(dblW * Math.log(Math.sqrt(real * real + imaginary * imaginary)));
	    
	    // 复数的实幂指数
	    return new Complex(r * Math.cos(dblW * t), r * Math.sin(dblW * t));
	}

	/**
	 * 计算复数的复幂指数
	 * 
	 * @param cpxW - 待求复幂指数的幂次
	 * @param n - 控制参数，默认值为0。当n=0时，求得的结果为复幂指数的主值
	 * @return Complex型，复数的复幂指数值
	 */
	public Complex pow(Complex cpxW, int n)
	{
		// 常量
		final double PI = 3.14159265358979;
		// 局部变量
	    double r, s, u, v;
	    
	    // 特殊值处理
	    if (real == 0)
		{
	        if (imaginary == 0)
				return new Complex(0, 0);
	            
	        s = 1.5707963268 * (Math.abs(imaginary) / imaginary + 4 * n);
		}
	    else
		{
	        s = 2 * PI * n + Math.atan2(imaginary, real);
	        
	        if (real < 0)
			{
	            if (imaginary > 0)
	                s = s + PI;
	            else
	                s = s - PI;
	        }
	    }
	    
	    // 求幂运算公式
	    r = 0.5 * Math.log(real * real + imaginary * imaginary);
	    v = cpxW.real * r + cpxW.imaginary * s;
	    u = Math.exp(cpxW.real * r - cpxW.imaginary * s);

	    return new Complex(u * Math.cos(v), u * Math.sin(v));
	}

	/**
	 * 计算复数的自然对数
	 * 
	 * @return Complex型，复数的自然对数值
	 */
	public Complex log()
	{
		double p = Math.log(Math.sqrt(real*real + imaginary*imaginary));
	    return new Complex(p, Math.atan2(imaginary, real));
	}

	/**
	 * 计算复数的正弦
	 * 
	 * @return Complex型，复数的正弦值
	 */
	public Complex sin()
	{
	    int i;
	    double x, y, y1, br, b1, b2;
	    double[] c = new double[6];
	    
	    // 切比雪夫公式的常数系数
	    c[0] = 1.13031820798497;
	    c[1] = 0.04433684984866;
	    c[2] = 0.00054292631191;
	    c[3] = 0.00000319843646;
	    c[4] = 0.00000001103607;
	    c[5] = 0.00000000002498;
	    
	    y1 = Math.exp(imaginary);
	    x = 0.5 * (y1 + 1 / y1);
	    br = 0;
	    if (Math.abs(imaginary) >= 1)
	        y = 0.5 * (y1 - 1 / y1);
	    else
	    {
			b1 = 0;
	        b2 = 0;
	        y1 = 2 * (2 * imaginary * imaginary - 1);
	        for (i = 5; i >=0; --i)
			{
	            br = y1 * b1 - b2 - c[i];
	            if (i != 0)
				{
	                b2 = b1;
	                b1 = br;
	            }
	        }
	        
	        y = imaginary * (br - b1);
	    }
	    
	    // 组合计算结果
	    x = x * Math.sin(real);
	    y = y * Math.cos(real);

		return new Complex(x, y);
	}

	/**
	 * 计算复数的余弦
	 * 
	 * @return Complex型，复数的余弦值
	 */
	public Complex cos()
	{
	    int i;
	    double x, y, y1, br, b1, b2;
	    double[] c = new double[6];
	    
	    // 切比雪夫公式的常数系数
	    c[0] = 1.13031820798497;
	    c[1] = 0.04433684984866;
	    c[2] = 0.00054292631191;
	    c[3] = 0.00000319843646;
	    c[4] = 0.00000001103607;
	    c[5] = 0.00000000002498;
	    
	    y1 = Math.exp(imaginary);
	    x = 0.5 * (y1 + 1 / y1);
	    br = 0;
	    if (Math.abs(imaginary) >= 1)
	        y = 0.5 * (y1 - 1 / y1);
	    else
	    {
			b1 = 0;
	        b2 = 0;
	        y1 = 2 * (2 * imaginary * imaginary - 1);
	        for (i=5 ; i>=0; --i)
			{
	            br = y1 * b1 - b2 - c[i];
	            if (i != 0)
	            {
					b2 = b1;
	                b1 = br;
	            }
	        }
	        
	        y = imaginary * (br - b1);
	    }
	    
	    // 组合计算结果
	    x = x * Math.cos(real);
		y = -y * Math.sin(real);

		return new Complex(x, y);
	}

	/**
	 * 计算复数的正切
	 * 
	 * @return Complex型，复数的正切值
	 */
	public Complex tan()
	{
		return sin().divide(cos());
	}
}
