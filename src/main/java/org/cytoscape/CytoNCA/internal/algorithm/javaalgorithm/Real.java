package org.cytoscape.CytoNCA.internal.algorithm.javaalgorithm;

/**
 * 用于传递值的实数类Real

 * @author 周长发
 * @version 1.0
 */
public class Real 
{
	private Double value = null;
	
	/**
	 * 基本构造函数
	 */
	public Real()
	{
	}

	/**
	 * 指定值构造函数
	 * 
	 * @param v Real型值
	 */
	public Real(Real v)
	{
		setValue(v);
	}

	/**
	 * 指定值构造函数
	 * 
	 * @param v int型值
	 */
	public Real(int v)
	{
		setValue(v);
	}

	/**
	 * 指定值构造函数
	 * 
	 * @param v short型值
	 */
	public Real(short v)
	{
		setValue(v);
	}

	/**
	 * 指定值构造函数
	 * 
	 * @param v long型值
	 */
	public Real(long v)
	{
		setValue(v);
	}

	/**
	 * 指定值构造函数
	 * 
	 * @param v float型值
	 */
	public Real(float v)
	{
		setValue(v);
	}

	/**
	 * 指定值构造函数
	 * 
	 * @param v double型值
	 */
	public Real(double v)
	{
		setValue(v);
	}

	/**
	 * 指定值构造函数
	 * 
	 * @param v String型值
	 */
	public Real(String v)
	{
		setValue(v);
	}

	/**
	 * 赋值函数
	 * 
	 * @param v Real型值
	 */
	public void setValue(Real v)
	{
		value = new Double(v.doubleValue());
	}

	/**
	 * 赋值函数
	 * 
	 * @param v int型值
	 */
	public void setValue(int v)
	{
		value = new Double(v);
	}

	/**
	 * 赋值函数
	 * 
	 * @param v long型值
	 */
	public void setValue(long v)
	{
		value = new Double(v);
	}

	/**
	 * 赋值函数
	 * 
	 * @param v short型值
	 */
	public void setValue(short v)
	{
		value = new Double(v);
	}
	
	/**
	 * 赋值函数
	 * 
	 * @param v float型值
	 */
	public void setValue(float v)
	{
		value = new Double(v);
	}

	/**
	 * 赋值函数
	 * 
	 * @param v double型值
	 */
	public void setValue(double v)
	{
		value = new Double(v);
	}

	/**
	 * 赋值函数
	 * 
	 * @param v String型值
	 */
	public void setValue(String v)
	{
		value = new Double(Double.parseDouble(v));
	}
	
	/**
	 * 转化为字符串函数
	 * 
	 * @return String型值
	 */
	public String toString()
	{
		return new Float(value.floatValue()).toString();
	}

	/**
	 * 取值函数
	 * 
	 * @return int型值
	 */
	public int intValue()
	{
		return value.intValue();
	}

	/**
	 * 取值函数
	 * 
	 * @return short型值
	 */
	public short shortValue()
	{
		return value.shortValue();
	}

	/**
	 * 取值函数
	 * 
	 * @return long型值
	 */
	public long longValue()
	{
		return value.longValue();
	}

	/**
	 * 取值函数
	 * 
	 * @return float型值
	 */
	public float floatValue()
	{
		return value.floatValue();
	}

	/**
	 * 取值函数
	 * 
	 * @return double型值
	 */
	public double doubleValue()
	{
		return value.doubleValue();
	}
}
