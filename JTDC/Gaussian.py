import sys
from scipy.optimize import curve_fit
from scipy import asarray as ar, exp
import numpy as np

if __name__ == '__main__':
    for linep in sys.stdin:
        popt = 'error'
        try:
            line = linep[:-1]
            dS = line.split(";")
            inputX = dS[0]
            inputY = dS[1]
            xData = [float(s) for s in inputX.split(",")]
            yData = [float(s) for s in inputY.split(",")]

            if max(yData) == 0:
                print('Error: all 0')
                sys.stdout.flush()
                continue


            def gaus(x, a, x0, sigma, b):
                return a * exp(-(x - x0) ** 2 / (2 * sigma ** 2)) + b


            def maxIndex(data):
                mI = 0
                for i in range(1, len(data)):
                    if data[i] > data[mI]:
                        mI = i
                return mI


            def firstIndexLargerThan(data, threshold):
                for i in range(0, len(data)):
                    if data[i] > threshold:
                        return i


            x = ar(xData)
            y = ar(yData)
            bottom = (sum(yData[:5]) + sum(yData[-5:])) / 10
            amp = max(yData) - bottom
            mean = xData[maxIndex(yData)]
            threshold = (max(yData) + bottom) / 2
            sigma = np.sqrt(sum(y * (x - mean) ** 2) / sum(y))
            popt, pcov = curve_fit(gaus, x, y, p0=[amp, mean, sigma, bottom])
        except RuntimeError as e:
            popt = 'Error: {}'.format(e)
        print(popt)
        sys.stdout.flush()
        exit()
