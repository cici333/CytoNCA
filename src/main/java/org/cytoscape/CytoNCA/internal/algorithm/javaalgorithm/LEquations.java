/*
 * 求解线性方程组的类 LEquations
 * 
 * 周长发编制
 */
package org.cytoscape.CytoNCA.internal.algorithm.javaalgorithm;

/**
 * 求解线性方程组的类 LEquations

 * @author 周长发
 * @version 1.0
 */
public class LEquations 
{
	private Matrix	mtxLECoef;		// 系数矩阵
	private Matrix mtxLEConst;		// 常数矩阵

	/**
	 * 基本构造函数
	 */
	public LEquations()
	{
	}

	/**
	 * 指定系数和常数构造函数
	 * 
	 * @param mtxCoef - 指定的系数矩阵
	 * @param mtxConst - 指定的常数矩阵
	 */
	public LEquations(Matrix mtxCoef, Matrix mtxConst)
	{
		init(mtxCoef, mtxConst);
	}

	/**
	 * 初始化函数
	 * 
	 * @param mtxCoef - 指定的系数矩阵
	 * @param mtxConst - 指定的常数矩阵
	 * @return boolean 型，初始化是否成功
	 */
	public boolean init(Matrix mtxCoef, Matrix mtxConst)
	{
		if (mtxCoef.getNumRows() != mtxConst.getNumRows())
			return false;

		mtxLECoef = new Matrix(mtxCoef);
		mtxLEConst = new Matrix(mtxConst);

		return true;
	}

	/**
	 * 获取系数矩阵
	 * 
	 * @return Matrix 型，返回系数矩阵
	 */
	public Matrix getCoefMatrix()
	{
		return mtxLECoef;
	}

	/**
	 * 获取常数矩阵
	 * 
	 * @return Matrix 型，返回系数矩阵
	 */
	public Matrix getConstMatrix()
	{
		return mtxLEConst;
	}

	/**
	 * 获取方程个数
	 * 
	 * @return int 型，返回方程组方程的个数
	 */
	public int getNumEquations()
	{
		return getCoefMatrix().getNumRows();
	}

	/**
	 * 获取未知数个数
	 * 
	 * @return int 型，返回方程组未知数的个数
	 */
	public int getNumUnknowns()
	{
		return getCoefMatrix().getNumColumns();
	}

	/**
	 * 全选主元高斯消去法
	 * 
	 * @param mtxResult - Matrix对象，返回方程组的解
	 * @return boolean 型，方程组求解是否成功
	 */
	public boolean getRootsetGauss(Matrix mtxResult)
	{ 
		int l,k,i,j,nIs=0,p,q;
	    double d,t;

		// 方程组的属性，将常数矩阵赋给解矩阵
		mtxResult.setValue(mtxLEConst);
		double[] pDataCoef = mtxLECoef.getData();
		double[] pDataConst = mtxResult.getData();
		int n = getNumUnknowns();

		// 临时缓冲区，存放列数
	    int[] pnJs = new int[n];

		// 消元
	    l=1;
	    for (k=0;k<=n-2;k++)
	    { 
			d=0.0;
	        for (i=k;i<=n-1;i++)
			{
				for (j=k;j<=n-1;j++)
	            { 
					t=Math.abs(pDataCoef[i*n+j]);
					if (t>d) 
					{ 
						d=t; 
						pnJs[k]=j; 
						nIs=i;
					}
	            }
			}

	        if (d == 0.0) 
				l=0;
	        else
	        { 
				if (pnJs[k]!=k)
	            {
					for (i=0;i<=n-1;i++)
	                { 
						p=i*n+k; 
						q=i*n+pnJs[k];
						t=pDataCoef[p]; 
						pDataCoef[p]=pDataCoef[q]; 
						pDataCoef[q]=t;
	                }
				}

	            if (nIs!=k)
	            { 
					for (j=k;j<=n-1;j++)
	                { 
						p=k*n+j; 
						q=nIs*n+j;
	                    t=pDataCoef[p]; 
						pDataCoef[p]=pDataCoef[q]; 
						pDataCoef[q]=t;
	                }
	                
					t=pDataConst[k]; 
					pDataConst[k]=pDataConst[nIs]; 
					pDataConst[nIs]=t;
	            }
	        }
	        
			// 求解失败
			if (l==0)
	        { 
	            return false;
	        }
	        
			d=pDataCoef[k*n+k];
	        for (j=k+1;j<=n-1;j++)
	        { 
				p=k*n+j; 
				pDataCoef[p]=pDataCoef[p]/d;
			}
	        
			pDataConst[k]=pDataConst[k]/d;
	        for (i=k+1;i<=n-1;i++)
	        { 
				for (j=k+1;j<=n-1;j++)
	            { 
					p=i*n+j;
	                pDataCoef[p]=pDataCoef[p]-pDataCoef[i*n+k]*pDataCoef[k*n+j];
	            }
	            
				pDataConst[i]=pDataConst[i]-pDataCoef[i*n+k]*pDataConst[k];
	        }
	    }
	    
		// 求解失败
		d=pDataCoef[(n-1)*n+n-1];
	    if (d == 0.0)
		{ 
			return false;
		}

		// 求解
	    pDataConst[n-1]=pDataConst[n-1]/d;
	    for (i=n-2;i>=0;i--)
	    { 
			t=0.0;
	        for (j=i+1;j<=n-1;j++)
				t=t+pDataCoef[i*n+j]*pDataConst[j];
	        pDataConst[i]=pDataConst[i]-t;
	    }
	    
		// 调整解的位置
		pnJs[n-1]=n-1;
	    for (k=n-1;k>=0;k--)
	    {
			if (pnJs[k]!=k)
	        { 
				t=pDataConst[k]; 
				pDataConst[k]=pDataConst[pnJs[k]]; 
				pDataConst[pnJs[k]]=t;
			}
		}

	    return true;
	}

	/**
	 * 全选主元高斯－约当消去法
	 * 
	 * @param mtxResult - Matrix对象，返回方程组的解
	 * @return boolean 型，方程组求解是否成功
	 */
	public boolean GetRootsetGaussJordan(Matrix mtxResult)
	{ 
		int l,k,i,j,nIs=0,p,q;
	    double d,t;

		// 方程组的属性，将常数矩阵赋给解矩阵
		mtxResult.setValue(mtxLEConst);
		double[] pDataCoef = mtxLECoef.getData();
		double[] pDataConst = mtxResult.getData();
		int n = getNumUnknowns();
		int m = mtxLEConst.getNumColumns();

		// 临时缓冲区，存放变换的列数
	    int[] pnJs = new int[n];

		// 消元
	    l=1;
	    for (k=0;k<=n-1;k++)
	    { 
			d=0.0;
	        for (i=k;i<=n-1;i++)
	        {
				for (j=k;j<=n-1;j++)
	            { 
					t=Math.abs(pDataCoef[i*n+j]);
					if (t>d) 
					{ 
						d=t; 
						pnJs[k]=j; 
						nIs=i;
					}
	            }
			}

	        if (d+1.0==1.0) 
				l=0;
	        else
	        { 
				if (pnJs[k]!=k)
	            {
					for (i=0;i<=n-1;i++)
	                { 
						p=i*n+k; 
						q=i*n+pnJs[k];
						t=pDataCoef[p]; 
						pDataCoef[p]=pDataCoef[q]; 
						pDataCoef[q]=t;
	                }
				}

	            if (nIs!=k)
	            { 
					for (j=k;j<=n-1;j++)
	                { 
						p=k*n+j; 
						q=nIs*n+j;
	                    t=pDataCoef[p]; 
						pDataCoef[p]=pDataCoef[q]; 
						pDataCoef[q]=t;
	                }
	                
					for (j=0;j<=m-1;j++)
	                { 
						p=k*m+j; 
						q=nIs*m+j;
	                    t=pDataConst[p]; 
						pDataConst[p]=pDataConst[q]; 
						pDataConst[q]=t;
	                }
	            }
	        }
	        
			// 求解失败
			if (l==0)
	        { 
	            return false;
	        }
	        
			d=pDataCoef[k*n+k];
	        for (j=k+1;j<=n-1;j++)
	        { 
				p=k*n+j; 
				pDataCoef[p]=pDataCoef[p]/d;
			}
	        
			for (j=0;j<=m-1;j++)
	        { 
				p=k*m+j; 
				pDataConst[p]=pDataConst[p]/d;
			}
	        
			for (j=k+1;j<=n-1;j++)
	        {
				for (i=0;i<=n-1;i++)
	            { 
					p=i*n+j;
					if (i!=k)
						pDataCoef[p]=pDataCoef[p]-pDataCoef[i*n+k]*pDataCoef[k*n+j];
	            }
			}

	        for (j=0;j<=m-1;j++)
	        {
				for (i=0;i<=n-1;i++)
				{ 
					p=i*m+j;
					if (i!=k)
					pDataConst[p]=pDataConst[p]-pDataCoef[i*n+k]*pDataConst[k*m+j];
				}
			}
	    }
	    
		// 调整
		for (k=n-1;k>=0;k--)
	    {
			if (pnJs[k]!=k)
			{
				for (j=0;j<=m-1;j++)
				{ 
					p=k*m+j; 
					q=pnJs[k]*m+j;
					t=pDataConst[p]; 
					pDataConst[p]=pDataConst[q]; 
					pDataConst[q]=t;
				}
			}
		}

		return true;
	}

	/**
	 * 复系数方程组的全选主元高斯消去法
	 * 
	 * @param mtxCoefImag - 系数矩阵的虚部矩阵
	 * @param mtxConstImag - 常数矩阵的虚部矩阵
	 * @param mtxResult - Matrix对象，返回方程组解矩阵的实部矩阵
	 * @param mtxResultImag - Matrix对象，返回方程组解矩阵的虚部矩阵
	 * @return boolean 型，方程组求解是否成功
	 */
	public boolean GetRootsetGauss(Matrix mtxCoefImag, Matrix mtxConstImag, Matrix mtxResult, Matrix mtxResultImag)
	{ 
		int l,k,i,j,nIs=0,u,v;
	    double p,q,s,d;

		// 方程组的属性，将常数矩阵赋给解矩阵
		mtxResult.setValue(mtxLEConst);
		mtxResultImag.setValue(mtxConstImag);
		double[] pDataCoef = mtxLECoef.getData();
		double[] pDataConst = mtxResult.getData();
		double[] pDataCoefImag = mtxCoefImag.getData();
		double[] pDataConstImag = mtxResultImag.getData();
		int n = getNumUnknowns();
		int m = mtxLEConst.getNumColumns();

		// 临时缓冲区，存放变换的列数
	    int[] pnJs = new int[n];
	    
		// 消元
		for (k=0;k<=n-2;k++)
	    { 
			d=0.0;
	        for (i=k;i<=n-1;i++)
	        {
				for (j=k;j<=n-1;j++)
				{ 
					u=i*n+j;
					p=pDataCoef[u]*pDataCoef[u]+pDataCoefImag[u]*pDataCoefImag[u];
					if (p>d) 
					{
						d=p;
						pnJs[k]=j;
						nIs=i;
					}
				}
	        }
	        
			// 求解失败
			if (d == 0.0)
			{
				return false;
	        }
	        
			if (nIs!=k)
	        { 
				for (j=k;j<=n-1;j++)
	            { 
					u=k*n+j; 
					v=nIs*n+j;
	                p=pDataCoef[u]; 
					pDataCoef[u]=pDataCoef[v]; 
					pDataCoef[v]=p;
	                p=pDataCoefImag[u]; 
					pDataCoefImag[u]=pDataCoefImag[v]; 
					pDataCoefImag[v]=p;
	            }
	            
				p=pDataConst[k]; 
				pDataConst[k]=pDataConst[nIs]; 
				pDataConst[nIs]=p;
	            p=pDataConstImag[k]; 
				pDataConstImag[k]=pDataConstImag[nIs]; 
				pDataConstImag[nIs]=p;
	        }
	        
			if (pnJs[k]!=k)
	        {
				for (i=0;i<=n-1;i++)
	            { 
					u=i*n+k; 
					v=i*n+pnJs[k];
					p=pDataCoef[u]; 
					pDataCoef[u]=pDataCoef[v]; 
					pDataCoef[v]=p;
					p=pDataCoefImag[u]; 
					pDataCoefImag[u]=pDataCoefImag[v]; 
					pDataCoefImag[v]=p;
	            }
			}

	        v=k*n+k;
	        for (j=k+1;j<=n-1;j++)
	        { 
				u=k*n+j;
	            p=pDataCoef[u]*pDataCoef[v]; 
				q=-pDataCoefImag[u]*pDataCoefImag[v];
	            s=(pDataCoef[v]-pDataCoefImag[v])*(pDataCoef[u]+pDataCoefImag[u]);
	            pDataCoef[u]=(p-q)/d; 
				pDataCoefImag[u]=(s-p-q)/d;
	        }
	        
			p=pDataConst[k]*pDataCoef[v]; 
			q=-pDataConstImag[k]*pDataCoefImag[v];
	        s=(pDataCoef[v]-pDataCoefImag[v])*(pDataConst[k]+pDataConstImag[k]);
	        pDataConst[k]=(p-q)/d; 
			pDataConstImag[k]=(s-p-q)/d;

	        for (i=k+1;i<=n-1;i++)
	        { 
				u=i*n+k;
	            for (j=k+1;j<=n-1;j++)
	            { 
					v=k*n+j; 
					l=i*n+j;
	                p=pDataCoef[u]*pDataCoef[v]; 
					q=pDataCoefImag[u]*pDataCoefImag[v];
	                s=(pDataCoef[u]+pDataCoefImag[u])*(pDataCoef[v]+pDataCoefImag[v]);
	                pDataCoef[l]=pDataCoef[l]-p+q;
	                pDataCoefImag[l]=pDataCoefImag[l]-s+p+q;
	            }
	            
				p=pDataCoef[u]*pDataConst[k]; 
				q=pDataCoefImag[u]*pDataConstImag[k];
	            s=(pDataCoef[u]+pDataCoefImag[u])*(pDataConst[k]+pDataConstImag[k]);
	            pDataConst[i]=pDataConst[i]-p+q; 
				pDataConstImag[i]=pDataConstImag[i]-s+p+q;
	        }
	    }
	    
		u=(n-1)*n+n-1;
	    d=pDataCoef[u]*pDataCoef[u]+pDataCoefImag[u]*pDataCoefImag[u];

		// 求解失败
	    if (d == 0.0)
		{
			return false;
	    }

		// 求解
	    p=pDataCoef[u]*pDataConst[n-1]; q=-pDataCoefImag[u]*pDataConstImag[n-1];
	    s=(pDataCoef[u]-pDataCoefImag[u])*(pDataConst[n-1]+pDataConstImag[n-1]);
	    pDataConst[n-1]=(p-q)/d; pDataConstImag[n-1]=(s-p-q)/d;

	    for (i=n-2;i>=0;i--)
	    {
			for (j=i+1;j<=n-1;j++)
			{ 
				u=i*n+j;
				p=pDataCoef[u]*pDataConst[j]; 
				q=pDataCoefImag[u]*pDataConstImag[j];
				s=(pDataCoef[u]+pDataCoefImag[u])*(pDataConst[j]+pDataConstImag[j]);
				pDataConst[i]=pDataConst[i]-p+q;
				pDataConstImag[i]=pDataConstImag[i]-s+p+q;
			}
	    }

		// 调整位置
	    pnJs[n-1]=n-1;
	    for (k=n-1;k>=0;k--)
	    {
			if (pnJs[k]!=k)
	        { 
				p=pDataConst[k]; 
				pDataConst[k]=pDataConst[pnJs[k]]; 
				pDataConst[pnJs[k]]=p;
				p=pDataConstImag[k]; 
				pDataConstImag[k]=pDataConstImag[pnJs[k]]; 
				pDataConstImag[pnJs[k]]=p;
	        }
		}

		return true;
	}

	/**
	 * 复系数方程组的全选主元高斯－约当消去法
	 * 
	 * @param mtxCoefImag - 系数矩阵的虚部矩阵
	 * @param mtxConstImag - 常数矩阵的虚部矩阵
	 * @param mtxResult - Matrix对象，返回方程组解矩阵的实部矩阵
	 * @param mtxResultImag - Matrix对象，返回方程组解矩阵的虚部矩阵
	 * @return boolean 型，方程组求解是否成功
	 */
	public boolean GetRootsetGaussJordan(Matrix mtxCoefImag, Matrix mtxConstImag, Matrix mtxResult, Matrix mtxResultImag)
	{
		int l,k,i,j,nIs=0,u,v;
	    double p,q,s,d;

		// 方程组的属性，将常数矩阵赋给解矩阵
		mtxResult.setValue(mtxLEConst);
		mtxResultImag.setValue(mtxConstImag);
		double[] pDataCoef = mtxLECoef.getData();
		double[] pDataConst = mtxResult.getData();
		double[] pDataCoefImag = mtxCoefImag.getData();
		double[] pDataConstImag = mtxResultImag.getData();
		int n = getNumUnknowns();
		int m = mtxLEConst.getNumColumns();

		// 临时缓冲区，存放变换的列数
	    int[] pnJs = new int[n];

		// 消元
	    for (k=0;k<=n-1;k++)
	    { 
			d=0.0;
	        for (i=k;i<=n-1;i++)
	        {
				for (j=k;j<=n-1;j++)
				{ 
					u=i*n+j;
					p=pDataCoef[u]*pDataCoef[u]+pDataCoefImag[u]*pDataCoefImag[u];
					if (p>d) 
					{
						d=p;
						pnJs[k]=j;
						nIs=i;
					}
				}
	        }
	        
			// 求解失败
			if (d == 0.0)
	        {
	            return false;
	        }
	        
			if (nIs!=k)
	        { 
				for (j=k;j<=n-1;j++)
	            { 
					u=k*n+j; 
					v=nIs*n+j;
	                p=pDataCoef[u]; 
					pDataCoef[u]=pDataCoef[v]; 
					pDataCoef[v]=p;
	                p=pDataCoefImag[u]; 
					pDataCoefImag[u]=pDataCoefImag[v]; 
					pDataCoefImag[v]=p;
	            }
	            
				for (j=0;j<=m-1;j++)
	            { 
					u=k*m+j; 
					v=nIs*m+j;
	                p=pDataConst[u]; 
					pDataConst[u]=pDataConst[v]; 
					pDataConst[v]=p;
	                p=pDataConstImag[u]; 
					pDataConstImag[u]=pDataConstImag[v]; 
					pDataConstImag[v]=p;
	            }
	        }
	        
			if (pnJs[k]!=k)
	        {
				for (i=0;i<=n-1;i++)
	            { 
					u=i*n+k; 
					v=i*n+pnJs[k];
					p=pDataCoef[u]; 
					pDataCoef[u]=pDataCoef[v]; 
					pDataCoef[v]=p;
					p=pDataCoefImag[u]; 
					pDataCoefImag[u]=pDataCoefImag[v]; 
					pDataCoefImag[v]=p;
	            }
			}

	        v=k*n+k;
	        for (j=k+1;j<=n-1;j++)
	        { 
				u=k*n+j;
	            p=pDataCoef[u]*pDataCoef[v]; 
				q=-pDataCoefImag[u]*pDataCoefImag[v];
	            s=(pDataCoef[v]-pDataCoefImag[v])*(pDataCoef[u]+pDataCoefImag[u]);
	            pDataCoef[u]=(p-q)/d; 
				pDataCoefImag[u]=(s-p-q)/d;
	        }
	        
			for (j=0;j<=m-1;j++)
	        { 
				u=k*m+j;
	            p=pDataConst[u]*pDataCoef[v]; 
				q=-pDataConstImag[u]*pDataCoefImag[v];
	            s=(pDataCoef[v]-pDataCoefImag[v])*(pDataConst[u]+pDataConstImag[u]);
	            pDataConst[u]=(p-q)/d; 
				pDataConstImag[u]=(s-p-q)/d;
	        }
	        
			for (i=0;i<=n-1;i++)
	        {
				if (i!=k)
	            { 
					u=i*n+k;
					for (j=k+1;j<=n-1;j++)
	                { 
						v=k*n+j; 
						l=i*n+j;
						p=pDataCoef[u]*pDataCoef[v]; 
						q=pDataCoefImag[u]*pDataCoefImag[v];
						s=(pDataCoef[u]+pDataCoefImag[u])*(pDataCoef[v]+pDataCoefImag[v]);
						pDataCoef[l]=pDataCoef[l]-p+q;
						pDataCoefImag[l]=pDataCoefImag[l]-s+p+q;
	                }
	            
					for (j=0;j<=m-1;j++)
					{ 
						l=i*m+j; 
						v=k*m+j;
						p=pDataCoef[u]*pDataConst[v]; q=pDataCoefImag[u]*pDataConstImag[v];
						s=(pDataCoef[u]+pDataCoefImag[u])*(pDataConst[v]+pDataConstImag[v]);
						pDataConst[l]=pDataConst[l]-p+q; 
						pDataConstImag[l]=pDataConstImag[l]-s+p+q;
					}
	            }
			}
	    }

		// 求解调整
		for (k=n-1;k>=0;k--)
	    {
			if (pnJs[k]!=k)
			{
				for (j=0;j<=m-1;j++)
				{ 
					u=k*m+j;
					v=pnJs[k]*m+j;
					p=pDataConst[u]; 
					pDataConst[u]=pDataConst[v]; 
					pDataConst[v]=p;
					p=pDataConstImag[u]; 
					pDataConstImag[u]=pDataConstImag[v]; 
					pDataConstImag[v]=p;
				}
			}
		}

		return true;
	}

	/**
	 * 求解三对角线方程组的追赶法
	 * 
	 * @param mtxResult - Matrix对象，返回方程组解矩阵
	 * @return boolean 型，方程组求解是否成功
	 */
	public boolean getRootsetTriDiagonal(Matrix mtxResult)
	{ 
		int k,j;
	    double s;
	    
		// 将常数矩阵赋给解矩阵
		mtxResult.setValue(mtxLEConst);
		double[] pDataConst = mtxResult.getData();

		int n = getNumUnknowns();
		if (mtxLECoef.getNumRows() != n)
			return false;

		// 为系数矩阵对角线数组分配内存
		double[] pDiagData = new double[3*n-2];

		// 构造系数矩阵对角线元素数组
		k = j = 0;
		if (k == 0)
		{
			pDiagData[j++] = mtxLECoef.getElement(k, k);
			pDiagData[j++] = mtxLECoef.getElement(k, k+1);
		}
	    for (k=1; k<n-1; ++k)
		{
			pDiagData[j++] = mtxLECoef.getElement(k, k-1);
			pDiagData[j++] = mtxLECoef.getElement(k, k);
			pDiagData[j++] = mtxLECoef.getElement(k, k+1);
		}
		if (k == n-1)
		{
			pDiagData[j++] = mtxLECoef.getElement(k, k-1);
			pDiagData[j++] = mtxLECoef.getElement(k, k);
		}

		// 求解
		for (k=0;k<=n-2;k++)
	    { 
			j=3*k; 
			s=pDiagData[j];

			// 求解失败
	        if (Math.abs(s)+1.0==1.0)
			{
				return false;
			}

	        pDiagData[j+1]=pDiagData[j+1]/s;
	        pDataConst[k]=pDataConst[k]/s;
	        pDiagData[j+3]=pDiagData[j+3]-pDiagData[j+2]*pDiagData[j+1];
	        pDataConst[k+1]=pDataConst[k+1]-pDiagData[j+2]*pDataConst[k];
	    }
	    
		s=pDiagData[3*n-3];
	    if (s == 0.0)
		{
			return false;
		}
	    
		// 调整
		pDataConst[n-1]=pDataConst[n-1]/s;
	    for (k=n-2;k>=0;k--)
			pDataConst[k]=pDataConst[k]-pDiagData[3*k+1]*pDataConst[k+1];
	    
		return true;
	}

	/**
	 * 一般带型方程组的求解
	 * 
	 * @param nBandWidth - 带宽
	 * @param mtxResult - Matrix对象，返回方程组解矩阵
	 * @return boolean 型，方程组求解是否成功
	 */
	public boolean getRootsetBand(int nBandWidth, Matrix mtxResult)
	{ 
		int ls,k,i,j,is=0,u,v;
	    double p,t;
	    
		// 带宽必须为奇数
		if ((nBandWidth-1)%2 != 0)
			return false;

		// 将常数矩阵赋给解矩阵
		mtxResult.setValue(mtxLEConst);
		double[] pDataConst = mtxResult.getData();

		// 方程组属性
		int m = mtxLEConst.getNumColumns();
		int n = getNumUnknowns();
		if (mtxLECoef.getNumRows() != n)
			return false;

		// 带宽数组：带型矩阵的有效数据
		double[] pBandData = new double[nBandWidth*n];

		// 半带宽
	    ls = (nBandWidth-1)/2;

		// 构造带宽数组
	    for (i=0; i<n; ++i)
		{
			j = 0;
			for (k=Math.max(0, i-ls); k<Math.max(0, i-ls)+nBandWidth; ++k)
			{
				if (k < n)
					pBandData[i*nBandWidth+j++] = mtxLECoef.getElement(i, k);
				else
					pBandData[i*nBandWidth+j++] = 0;
			}
		}

		// 求解
	    for (k=0;k<=n-2;k++)
	    { 
			p=0.0;
	        for (i=k;i<=ls;i++)
	        { 
				t=Math.abs(pBandData[i*nBandWidth]);
	            if (t>p) 
				{
					p=t; 
					is=i;
				}
	        }
	        
			if (p == 0.0)
			{
				return false;
			}

	        for (j=0;j<=m-1;j++)
	        { 
				u=k*m+j; 
				v=is*m+j;
	            t=pDataConst[u]; 
				pDataConst[u]=pDataConst[v]; 
				pDataConst[v]=t;
	        }
	        
			for (j=0;j<=nBandWidth-1;j++)
	        { 
				u=k*nBandWidth+j; 
				v=is*nBandWidth+j;
	            t=pBandData[u]; 
				pBandData[u]=pBandData[v]; 
				pBandData[v]=t;
	        }
	        
			for (j=0;j<=m-1;j++)
	        { 
				u=k*m+j; 
				pDataConst[u]=pDataConst[u]/pBandData[k*nBandWidth];
			}
	        
			for (j=1;j<=nBandWidth-1;j++)
	        { 
				u=k*nBandWidth+j; 
				pBandData[u]=pBandData[u]/pBandData[k*nBandWidth];
			}
	        
			for (i=k+1;i<=ls;i++)
	        { 
				t=pBandData[i*nBandWidth];
	            for (j=0;j<=m-1;j++)
	            { 
					u=i*m+j; 
					v=k*m+j;
	                pDataConst[u]=pDataConst[u]-t*pDataConst[v];
	            }
	            
				for (j=1;j<=nBandWidth-1;j++)
	            { 
					u=i*nBandWidth+j; 
					v=k*nBandWidth+j;
	                pBandData[u-1]=pBandData[u]-t*pBandData[v];
	            }
	            
				u=i*nBandWidth+nBandWidth-1; pBandData[u]=0.0;
	        }
	        
			if (ls!=(n-1)) 
				ls=ls+1;
	    }
	    
		p=pBandData[(n-1)*nBandWidth];
	    if (p == 0.0)
		{
			return false;
		}

	    for (j=0;j<=m-1;j++)
	    { 
			u=(n-1)*m+j; 
			pDataConst[u]=pDataConst[u]/p;
		}
	    
		ls=1;
	    for (i=n-2;i>=0;i--)
	    { 
			for (k=0;k<=m-1;k++)
	        { 
				u=i*m+k;
	            for (j=1;j<=ls;j++)
	            { 
					v=i*nBandWidth+j; 
					is=(i+j)*m+k;
	                pDataConst[u]=pDataConst[u]-pBandData[v]*pDataConst[is];
	            }
	        }
	        
			if (ls!=(nBandWidth-1)) 
				ls=ls+1;
	    }
	    
		return true;
	}

	/**
	 * 求解对称方程组的分解法
	 * 
	 * @param mtxResult - CMatrix引用对象，返回方程组解矩阵
	 * @return boolean 型，方程组求解是否成功
	 */
	public boolean getRootsetDjn(Matrix mtxResult)
	{ 
		int i,j,l,k,u,v,w,k1,k2,k3;
	    double p;

		// 方程组属性，将常数矩阵赋给解矩阵
		Matrix mtxCoef = new Matrix(mtxLECoef);
		mtxResult.setValue(mtxLEConst);
		int n = mtxCoef.getNumColumns();
		int m = mtxResult.getNumColumns();
		double[] pDataCoef = mtxCoef.getData();
		double[] pDataConst = mtxResult.getData();

		// 非对称系数矩阵，不能用本方法求解
	    if (pDataCoef[0] == 0.0)
			return false;

	    for (i=1; i<=n-1; i++)
	    { 
			u=i*n; 
			pDataCoef[u]=pDataCoef[u]/pDataCoef[0];
		}
	    
		for (i=1; i<=n-2; i++)
	    { 
			u=i*n+i;
	        for (j=1; j<=i; j++)
	        { 
				v=i*n+j-1; 
				l=(j-1)*n+j-1;
	            pDataCoef[u]=pDataCoef[u]-pDataCoef[v]*pDataCoef[v]*pDataCoef[l];
	        }
	        
			p=pDataCoef[u];
	        if (p == 0.0)
				return false;

	        for (k=i+1; k<=n-1; k++)
	        { 
				u=k*n+i;
	            for (j=1; j<=i; j++)
	            { 
					v=k*n+j-1; 
					l=i*n+j-1; 
					w=(j-1)*n+j-1;
					pDataCoef[u]=pDataCoef[u]-pDataCoef[v]*pDataCoef[l]*pDataCoef[w];
	            }
	            
				pDataCoef[u]=pDataCoef[u]/p;
	        }
	    }
	    
		u=n*n-1;
	    for (j=1; j<=n-1; j++)
	    { 
			v=(n-1)*n+j-1; 
			w=(j-1)*n+j-1;
	        pDataCoef[u]=pDataCoef[u]-pDataCoef[v]*pDataCoef[v]*pDataCoef[w];
	    }
	    
		p=pDataCoef[u];
	    if (p == 0.0)
			return false;

	    for (j=0; j<=m-1; j++)
		{
		    for (i=1; i<=n-1; i++)
			{ 
				u=i*m+j;
				for (k=1; k<=i; k++)
				{ 
					v=i*n+k-1; 
					w=(k-1)*m+j;
					pDataConst[u]=pDataConst[u]-pDataCoef[v]*pDataConst[w];
				}
			}
		}

	    for (i=1; i<=n-1; i++)
	    { 
			u=(i-1)*n+i-1;
	        for (j=i; j<=n-1; j++)
	        { 
				v=(i-1)*n+j; 
				w=j*n+i-1;
	            pDataCoef[v]=pDataCoef[u]*pDataCoef[w];
	        }
	    }
	    
		for (j=0; j<=m-1; j++)
	    { 
			u=(n-1)*m+j;
	        pDataConst[u]=pDataConst[u]/p;
	        for (k=1; k<=n-1; k++)
	        { 
				k1=n-k; 
				k3=k1-1; 
				u=k3*m+j;
	            for (k2=k1; k2<=n-1; k2++)
	            { 
					v=k3*n+k2; 
					w=k2*m+j;
	                pDataConst[u]=pDataConst[u]-pDataCoef[v]*pDataConst[w];
	            }
	            
				pDataConst[u]=pDataConst[u]/pDataCoef[k3*n+k3];
	        }
	    }
	    
		return true;
	}

	/**
	 * 求解对称正定方程组的平方根法
	 * 
	 * @param mtxResult - CMatrix引用对象，返回方程组解矩阵
	 * @return boolean 型，方程组求解是否成功
	 */
	public boolean getRootsetCholesky(Matrix mtxResult)
	{ 
		int i,j,k,u,v;
	    
		// 方程组属性，将常数矩阵赋给解矩阵
		Matrix mtxCoef = new Matrix(mtxLECoef);
		mtxResult.setValue(mtxLEConst);
		int n = mtxCoef.getNumColumns();
		int m = mtxResult.getNumColumns();
		double[] pDataCoef = mtxCoef.getData();
		double[] pDataConst = mtxResult.getData();
	    
		// 非对称正定系数矩阵，不能用本方法求解
		if (pDataCoef[0] <= 0.0)
			return false;

		pDataCoef[0]=Math.sqrt(pDataCoef[0]);
	    for (j=1; j<=n-1; j++) 
			pDataCoef[j]=pDataCoef[j]/pDataCoef[0];
	    
		for (i=1; i<=n-1; i++)
	    { 
			u=i*n+i;
	        for (j=1; j<=i; j++)
	        { 
				v=(j-1)*n+i;
	            pDataCoef[u]=pDataCoef[u]-pDataCoef[v]*pDataCoef[v];
	        }
	        
			if (pDataCoef[u] <= 0.0)
				return false;

	        pDataCoef[u]=Math.sqrt(pDataCoef[u]);
	        if (i!=(n-1))
	        { 
				for (j=i+1; j<=n-1; j++)
	            { 
					v=i*n+j;
	                for (k=1; k<=i; k++)
						pDataCoef[v]=pDataCoef[v]-pDataCoef[(k-1)*n+i]*pDataCoef[(k-1)*n+j];
	                pDataCoef[v]=pDataCoef[v]/pDataCoef[u];
	            }
	        }
	    }
	    
		for (j=0; j<=m-1; j++)
	    { 
			pDataConst[j]=pDataConst[j]/pDataCoef[0];
	        for (i=1; i<=n-1; i++)
	        { 
				u=i*n+i; 
				v=i*m+j;
	            for (k=1; k<=i; k++)
					pDataConst[v]=pDataConst[v]-pDataCoef[(k-1)*n+i]*pDataConst[(k-1)*m+j];
	            pDataConst[v]=pDataConst[v]/pDataCoef[u];
	        }
	    }
	    
		for (j=0; j<=m-1; j++)
	    { 
			u=(n-1)*m+j;
	        pDataConst[u]=pDataConst[u]/pDataCoef[n*n-1];
	        for (k=n-1; k>=1; k--)
	        { 
				u=(k-1)*m+j;
	            for (i=k; i<=n-1; i++)
	            { 
					v=(k-1)*n+i;
	                pDataConst[u]=pDataConst[u]-pDataCoef[v]*pDataConst[i*m+j];
	            }
	            
				v=(k-1)*n+k-1;
	            pDataConst[u]=pDataConst[u]/pDataCoef[v];
	        }
	    }
	    
		return true;
	}

	/**
	 * 求解大型稀疏方程组的全选主元高斯－约去消去法
	 * 
	 * @param mtxResult - CMatrix引用对象，返回方程组解矩阵
	 * @return boolean 型，方程组求解是否成功
	 */
	public boolean getRootsetGgje(Matrix mtxResult)
	{ 
		int i,j,k,nIs=0,u,v;
	    double d,t;
	    
		// 方程组属性，将常数矩阵赋给解矩阵
		Matrix mtxCoef = new Matrix(mtxLECoef);
		mtxResult.setValue(mtxLEConst);
		int n = mtxCoef.getNumColumns();
		double[] pDataCoef = mtxCoef.getData();
		double[] pDataConst = mtxResult.getData();

		// 临时缓冲区，存放变换的列数
		int[] pnJs = new int[n];

		// 消元
	    for (k=0; k<=n-1; k++)
	    { 
			d=0.0;
	        for (i=k; i<=n-1; i++)
			{
				for (j=k; j<=n-1; j++)
				{ 
					t=Math.abs(pDataCoef[i*n+j]);
					if (t>d) 
					{
						d=t; 
						pnJs[k]=j; 
						nIs=i;
					}
				}
			}

	        if (d == 0.0)
			{
				return false;
			}

	        if (nIs!=k)
	        { 
				for (j=k; j<=n-1; j++)
	            { 
					u=k*n+j; 
					v=nIs*n+j;
	                t=pDataCoef[u]; 
					pDataCoef[u]=pDataCoef[v]; 
					pDataCoef[v]=t;
	            }
	            
				t=pDataConst[k]; 
				pDataConst[k]=pDataConst[nIs]; 
				pDataConst[nIs]=t;
	        }
	        
			if (pnJs[k]!=k)
	        {
				for (i=0; i<=n-1; i++)
	            { 
					u=i*n+k; 
					v=i*n+pnJs[k];
					t=pDataCoef[u]; 
					pDataCoef[u]=pDataCoef[v]; 
					pDataCoef[v]=t;
	            }
			}

	        t=pDataCoef[k*n+k];
	        for (j=k+1; j<=n-1; j++)
	        { 
				u=k*n+j;
	            if (pDataCoef[u]!=0.0) 
					pDataCoef[u]=pDataCoef[u]/t;
	        }
	        
			pDataConst[k]=pDataConst[k]/t;
	        for (j=k+1; j<=n-1; j++)
	        { 
				u=k*n+j;
	            if (pDataCoef[u]!=0.0)
	            { 
					for (i=0; i<=n-1; i++)
	                { 
						v=i*n+k;
	                    if ((i!=k)&&(pDataCoef[v]!=0.0))
	                    { 
							nIs=i*n+j;
	                        pDataCoef[nIs]=pDataCoef[nIs]-pDataCoef[v]*pDataCoef[u];
	                    }
	                }
	            }
	        }
	        
			for (i=0; i<=n-1; i++)
	        { 
				u=i*n+k;
	            if ((i!=k)&&(pDataCoef[u]!=0.0))
					pDataConst[i]=pDataConst[i]-pDataCoef[u]*pDataConst[k];
	        }
	    }
	    
		// 调整
		for (k=n-1; k>=0; k--)
	    {
			if (k!=pnJs[k])
	        { 
				t=pDataConst[k]; 
				pDataConst[k]=pDataConst[pnJs[k]]; 
				pDataConst[pnJs[k]]=t;
			}
		}

		return true;
	}

	/**
	 * 求解托伯利兹方程组的列文逊方法
	 * 
	 * @param mtxResult - CMatrix引用对象，返回方程组解矩阵
	 * @return boolean 型，方程组求解是否成功
	 */
	public boolean getRootsetTlvs(Matrix mtxResult)
	{ 
		int i,j,k;
	    double a,beta,q,c,h;

		// 未知数个数
	    int n = mtxLECoef.getNumColumns();

		// 初始化解解向量
		mtxResult.init(n, 1);
		double[] x = mtxResult.getData();

		// 常数数组
		double[] pDataConst = mtxLEConst.getData();

		// 建立T数组
		double[] t = new double[n];

		// 构造T数组
	    for (i=0; i<n; ++i)
			t[i] = mtxLECoef.getElement(0, i);

		// 临时数组
		double[] s = new double[n];
		double[] y = new double[n];

		// 非托伯利兹方程组，不能用本方法求解
	    a=t[0];
	    if (a == 0.0)
	    { 
			return false;
		}

		// 列文逊方法求解
	    y[0]=1.0; 
		x[0]=pDataConst[0]/a;
	    for (k=1; k<=n-1; k++)
	    { 
			beta=0.0; 
			q=0.0;
	        for (j=0; j<=k-1; j++)
	        { 
				beta=beta+y[j]*t[j+1];
	            q=q+x[j]*t[k-j];
	        }
	        
			if (a == 0.0)
			{ 
				return false;
			}

	        c=-beta/a; 
			s[0]=c*y[k-1]; 
			y[k]=y[k-1];
	        if (k!=1)
	        {
				for (i=1; i<=k-1; i++)
					s[i]=y[i-1]+c*y[k-i-1];
			}

	        a=a+c*beta;
			if (a == 0.0)
			{ 
				return false;
			}

	        h=(pDataConst[k]-q)/a;
	        for (i=0; i<=k-1; i++)
	        { 
				x[i]=x[i]+h*s[i]; 
				y[i]=s[i];
			}
	        
			x[k]=h*y[k];
	    }
	    
	    return true;
	}

	/**
	 * 高斯－赛德尔迭代法
	 *  
	 * @param mtxResult - CMatrix引用对象，返回方程组解矩阵
	 * @param eps - 控制精度
	 * @return boolean 型，方程组求解是否成功
	 */
	public boolean getRootsetGaussSeidel(Matrix mtxResult, double eps)
	{ 
		int i,j,u,v;
	    double p,t,s,q;

		// 未知数个数
	    int n = mtxLECoef.getNumColumns();

		// 初始化解向量
		mtxResult.init(n, 1);
		double[] x = mtxResult.getData();

		// 系数与常数
		double[] pDataCoef = mtxLECoef.getData();
		double[] pDataConst = mtxLEConst.getData();
	    
		// 求解
		for (i=0; i<=n-1; i++)
	    { 
			u=i*n+i; 
			p=0.0; 
			x[i]=0.0;
	        for (j=0; j<=n-1; j++)
	        {
				if (i!=j)
	            { 
					v=i*n+j; 
					p=p+Math.abs(pDataCoef[v]);
				}
			}

	        if (p>=Math.abs(pDataCoef[u]))
				return false;
	    }

		// 精度控制
	    p=eps+1.0;
	    while (p>=eps)
	    { 
			p=0.0;
	        for (i=0; i<=n-1; i++)
	        { 
				t=x[i]; 
				s=0.0;
	            for (j=0; j<=n-1; j++)
					if (j!=i) 
						s=s+pDataCoef[i*n+j]*x[j];

	            x[i]=(pDataConst[i]-s)/pDataCoef[i*n+i];
	            q=Math.abs(x[i]-t)/(1.0+Math.abs(x[i]));
	            if (q>p) 
					p=q;
	        }
	    }
	    
		return true;
	}

	/**
	 * 求解对称正定方程组的共轭梯度法
	 * 
	 * @param mtxResult - CMatrix引用对象，返回方程组解矩阵
	 * @param eps - 控制精度
	 */
	public void getRootsetGrad(Matrix mtxResult, double eps)
	{ 
		int i,k;
	    double alpha,beta,d,e;

		// 未知数个数
	    int n = getNumUnknowns();

		// 初始化解向量
		mtxResult.init(n, 1);
		double[] x = mtxResult.getData();

		// 构造临时矩阵
		Matrix mtxP = new Matrix(n, 1);
		double[] p = mtxP.getData();

		double[] pDataCoef = mtxLECoef.getData();
		double[] pDataConst = mtxLEConst.getData();

	    double[] r = new double[n];

	    for (i=0; i<=n-1; i++)
	    { 
			x[i]=0.0; 
			p[i]=pDataConst[i]; 
			r[i]=pDataConst[i]; 
		}
	    
		i=0;
	    while (i<=n-1)
	    { 
			Matrix mtxS = mtxLECoef.multiply(mtxP);
			double[] s = mtxS.getData();
	        
			d=0.0; 
			e=0.0;
	        for (k=0; k<=n-1; k++)
	        { 
				d=d+p[k]*pDataConst[k]; 
				e=e+p[k]*s[k]; 
			}
	        
			alpha=d/e;
	        for (k=0; k<=n-1; k++)
				x[k]=x[k]+alpha*p[k];
	        
			Matrix mtxQ = mtxLECoef.multiply(mtxResult);
		    double[] q = mtxQ.getData();
	        
			d=0.0;
	        for (k=0; k<=n-1; k++)
	        { 
				r[k]=pDataConst[k]-q[k]; 
				d=d+r[k]*s[k]; 
			}
	        
			beta=d/e; d=0.0;
	        for (k=0; k<=n-1; k++) 
				d=d+r[k]*r[k];
	        
			// 满足精度，求解结束
			d=Math.sqrt(d);
	        if (d<eps)
				break;

	        for (k=0; k<=n-1; k++)
				p[k]=r[k]-beta*p[k];
	        
			i=i+1;
	    }
	}

	/**
	 * 求解线性最小二乘问题的豪斯荷尔德变换法
	 * 
	 * @param mtxResult - Matrix对象，返回方程组解矩阵
	 * @param mtxQ - Matrix对象，返回豪斯荷尔德变换的Q矩阵
	 * @param mtxR - Matrix对象，返回豪斯荷尔德变换的R矩阵
	 * @return boolean 型，方程组求解是否成功
	 */
	public boolean getRootsetMqr(Matrix mtxResult, Matrix mtxQ, Matrix mtxR)
	{ 
		int i,j;
	    double d;

		// 方程组的方程数和未知数个数
	    int m = mtxLECoef.getNumRows();
	    int n = mtxLECoef.getNumColumns();
		// 奇异方程组
		if (m < n)
			return false;

		// 将解向量初始化为常数向量
		mtxResult.setValue(mtxLEConst);
		double[] pDataConst = mtxResult.getData();

		// 构造临时矩阵，用于QR分解
		mtxR.setValue(mtxLECoef);
		double[] pDataCoef = mtxR.getData();

		// QR分解
	    if (! mtxR.splitQR(mtxQ))
			return false;

		// 临时缓冲区
		double[] c = new double[n];
		double[] q = mtxQ.getData();

		// 求解
	    for (i=0; i<=n-1; i++)
	    { 
			d=0.0;
	        for (j=0; j<=m-1; j++)
				d=d+q[j*m+i]*pDataConst[j];
	    
			c[i]=d;
	    }
	    
		pDataConst[n-1]=c[n-1]/pDataCoef[n*n-1];
	    for (i=n-2; i>=0; i--)
	    { 
			d=0.0;
	        for (j=i+1; j<=n-1; j++)
				d=d+pDataCoef[i*n+j]*pDataConst[j];
	        
			pDataConst[i]=(c[i]-d)/pDataCoef[i*n+i];
	    }
	    
		return true;
	}

	/**
	 * 求解线性最小二乘问题的广义逆法
	 * 
	 * @param mtxResult - Matrix对象，返回方程组解矩阵
	 * @param mtxAP - Matrix对象，返回系数矩阵的广义逆矩阵
	 * @param mtxU - Matrix对象，返回U矩阵
	 * @param mtxV - Matrix对象，返回V矩阵
	 * @param eps - 控制精度
	 * @return boolean 型，方程组求解是否成功
	 */
	public boolean getRootsetGinv(Matrix mtxResult, Matrix mtxAP, Matrix mtxU, Matrix mtxV, double eps)
	{ 
		int i,j;
	    
		// 方程个数和未知数个数
	    int m = mtxLECoef.getNumRows();
	    int n = mtxLECoef.getNumColumns();

		// 初始化解向量
		mtxResult.init(n, 1);

		double[] pDataConst = mtxLEConst.getData();
		double[] x = mtxResult.getData();

		// 临时矩阵
		Matrix mtxA = new Matrix(mtxLECoef);

		// 求广义逆矩阵
	    if (! mtxA.invertUV(mtxAP, mtxU, mtxV, eps))
			return false;

		double[] pAPData = mtxAP.getData();

		// 求解
	    for (i=0; i<=n-1; i++)
	    { 
			x[i]=0.0;
	        for (j=0; j<=m-1; j++)
				x[i]=x[i]+pAPData[i*m+j]*pDataConst[j];
	    }
	    
		return true;
	}

	/**
	 * 
	 * @param mtxResult - Matrix对象，返回方程组解矩阵
	 * @param nMaxIt - 叠加次数
	 * @param eps - 控制精度
	 * @return boolean 型，方程组求解是否成功
	 */
	public boolean getRootsetMorbid(Matrix mtxResult, int nMaxIt /*= 60*/, double eps)
	{ 
		int i, k;
	    double q, qq;
	    
		// 方程的阶数
	    int n = getNumUnknowns();

		// 设定迭代次数, 缺省为60
		i = nMaxIt;
	    
		// 用全选主元高斯消元法求解
		LEquations leqs = new LEquations(mtxLECoef, mtxLEConst);
		if (! leqs.getRootsetGauss(mtxResult))
			return false;
		double[] x = mtxResult.getData();
	    
		q=1.0+eps;
	    while (q>=eps)
	    { 
			// 迭代次数已达最大值，仍为求得结果，求解失败
			if (i==0)
				return false;
	        
			// 迭代次数减1
			i=i-1;
			
			// 矩阵运算
			Matrix mtxE = mtxLECoef.multiply(mtxResult);
			Matrix mtxR = mtxLEConst.subtract(mtxE);

			// 用全选主元高斯消元法求解
			leqs = new LEquations(mtxLECoef, mtxR);
			Matrix mtxRR = new Matrix();
			if (! leqs.getRootsetGauss(mtxRR))
				return false;

			double[] r = mtxRR.getData();
	        
			q=0.0;
	        for ( k=0; k<=n-1; k++)
	        { 
				qq=Math.abs(r[k])/(1.0+Math.abs(x[k]+r[k]));
	            if (qq>q) 
					q=qq;
	        }
	        
			for ( k=0; k<=n-1; k++) 
				x[k]=x[k]+r[k];

	    }
	    
		// 求解成功
		return true;
	}
}
