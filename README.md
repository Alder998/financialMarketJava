# Financial Portfolio Optimization with Java

Java/Spring Project to apply Portfolio Optimization Techniques with real-time multi-asset financial data.

### 1. Financial Market Data

Yahoo Finance API is not working anymore on Java. For this reason, it has been necessary to create an ad-hoc solution to import, store, and process financial Data directly from Yahoo Finance. Therefore, I created a **small scraping library** that download the stocks returns, accounting, as well, for stock splits, dividends, and other special events.

### 2. Processing, Calculations, Optimization

Once the data have been obtained, the **main Mean-Variance Portfolio Optimization drivers are computed**. The Calculation Module contains utils-like methods to compute the average Returns, and Variance-Covariance Matrices. the drivers, then, are stored in a local Database using JDBC. Since Yahoo Finance restricts one single device to send a limited number of requests, stocks and bonds returns are **momentary stored in cache**. 

Once that the main drivers has been computed and stored, the library OjAlgo has been used to optimize the financial Portfolio. A **nested Optimization Technique** has been employed to allow multi-asset Portfolio Optimization.

### 3. Criticality and Future Developments

The general aim of this project was to improve the Optimization Techniques, to obtain Portfolios with higher returns and lower variance, since the current Algorithm creates Portfolios just minimizing the Risk through a Variance-Covariance Matrix.
The nearest next step could be to include Returns in the optimization Algorithm, maximizing the Sharpe Ratio. Personally, I would love to try to **change the Risk Metrics** from the Variance-Covariance Matrix, to a VaR or CVaR, using a **non-linear dependency Structure** (for example, through Copulas).

At the moment, this project needs to be suspended, since the Scraping Algorithm has been blocked (returning a 404 Error).

