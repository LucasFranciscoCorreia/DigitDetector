<h1 align="center" id="title">DigitDetector</h1>

<p align="center"><img src="https://socialify.git.ci/LucasFranciscoCorreia/DigitDetector/image?description=1&amp;forks=1&amp;issues=1&amp;language=1&amp;name=1&amp;owner=1&amp;pattern=Circuit+Board&amp;pulls=1&amp;stargazers=1&amp;theme=Auto" alt="project-image"></p>

<p id="description">This repository is designed as a project for Artificial Intelligence at UFRPE offered in 2018.1. This project has as a objective to detect digits in a 28x28 gray-scale image.</p>

<p align="center"><img src="https://img.shields.io/github/downloads/LucasFranciscoCorreia/DigitDetector/total" alt="shields"><img src="https://img.shields.io/github/issues/LucasFranciscoCorreia/DigitDetector" alt="shields"><img src="https://img.shields.io/github/issues-pr/LucasFranciscoCorreia/DigitDetector" alt="shields"><img src="https://img.shields.io/github/license/LucasFranciscoCorreia/DigitDetector" alt="shields"><img src="https://img.shields.io/github/repo-size/LucasFranciscoCorreia/DigitDetector" alt="shields"></p>

<h2>🛠️ Installation Steps:</h2>

<p>1. Download and Install JDK 23</p>

```
https://bell-sw.com/pages/downloads/#jdk-21-lts
```

<p>2. Download this repository</p>

```
git clone https://github.com/LucasFranciscoCorreia/DigitDetector.git
```

<p>3. Go to the project directory</p>

```
cd DigitDetector
```

<p>4. Run the demo</p>

```bash
java -jar ....jar
```

<h1>Digit Detection</h1>

<p>The following content has as objective demonstrate the algorithms used in this project to detect the digits in the images.</p>

<h2>Algorithms</h3>

<p>The algorithm choosed to this project was <strong>K Nearest Neighbours</strong> (<strong>KNN</strong>), where, when given an image, we search for <strong>K</strong> images from the database that are the most ressemble the image. After selecting <strong>K</strong> differents images from the database, we look at the digits they contain and then choose the digits that appears the most in the group. For any draw in the digit decision, a random digit between the highest amount of found digits is chosen as the nearest neighbour</p>

<div align="center">

<img src="https://raw.githubusercontent.com/LucasFranciscoCorreia/DigitDetector/refs/heads/master/readme/KNN.webp" width="750em">
</div>

<h2>Distance Algorithms</h2>
<p>The following sections describes the distance algorithms used for this projects, being them: <strong>Manhattan Distance</strong>, <strong>Euclidean Distance</strong> and <strong>Cosine Similarity</strong></p>

<h3>Manhattan Distance</h3>

<p>The <strong>Manhattan Distance</strong> is a metric used to determine the distance between 2 points, measuring the shortest as the sum of the absolute difference between the coordinates of the points</p>
<p>In order to calculate the distance between 2 images as a points, we take each cell of the 28x28 of the image and treat it as if it was a vector with 784 dimensions.</p>

<div align="center">

<img src="https://github.com/LucasFranciscoCorreia/DigitDetector/blob/master/readme/Manhattam%20Distance.png?raw=true" width="750em">

</div>

<h3>Euclidean Distance</h3>

<p>The <strong>Euclidean Distance</strong> is a metric used to determine the distance between 2 points, measuring the shortest distance as the length of the line segment that connect them.</p>

<p>As much as the <strong>Manhattan Distance</strong>, the <strong>Euclidean Distance</strong> treats the image as a vector with 784 dimensions to calculate the distance between them.</p>

<div align="center">

<img src="https://github.com/LucasFranciscoCorreia/DigitDetector/blob/master/readme/Euclidean%20Distance.png?raw=true" width="750em">

</div>

<h3>Cosine Similarity</h3>

<p>Unlike the <strong>Manhattan</strong> and <strong>Euclidean Distance</strong>, the <strong>Cosine Similarity</strong> measures the similarity of 2 vectors, which can also called as the angle between the 2 vectors. The closest the angle is to 0º, the nearest are the vectors. As the cosine function ranges from -1 to 1, and we want the closest images from the angle 0º, we'll seek for the images where the cosine off their angle are the closest to the highest value of the cosine function, 1. </p>

<div align="center">

<img src="https://github.com/LucasFranciscoCorreia/DigitDetector/blob/master/readme/Cosine%20Similarity.png?raw=true" width="750em">
<img src="https://github.com/LucasFranciscoCorreia/DigitDetector/blob/master/readme/Cosine%20Similarity%20Formula.png?raw=true" width="750em">

</div>

<h1>Validation</h1>

<p>The following content has as objetive demonstrate the validation and comparison between all the 3 distance algorithms. </p>

<p>In this project, we have a database of 60.000 handwritten images, with about 5.500~6.500 images for each digit. The database was originally in a 100MB+ .arff file, but were compressed and serialized in a 17MB .bin file in order to be pushed to Github</p>

<p>In order to find the optimal solution, we ran this validation 10 times, were each time we picked 100 more than the last iteration (100 for the first iteration, 200 for the second iteration, 300 for the third iteration, etc). Each iteration has their data collected 30 times to find an average value.</p>

<p>The split between train and test for the following validations were 90% to 10% (90% to train and 10% to test)</p>

<h2>Accuracy</h2>

The following graph presents the average accuracy between each measure system, based on the total amount of data for train an test. For example, a 5.000 amount of digits means 4500 digits splits for train an 500 digits splits for test.

<div align="center">
    <img src="https://github.com/LucasFranciscoCorreia/DigitDetector/blob/master/readme/Accuracy.png?raw=true" width="1000em">
</div>

<h2>Time</h2>

The following graph presents the average time between each measure system

<div align="center">
    <img src="https://github.com/LucasFranciscoCorreia/DigitDetector/blob/master/readme/Time.png?raw=true" width="1000em">
</div>

<h2>Precision</h2>

The following graphs presents the average precision for each digit for each measure. 

<div align="center">
    <img src="https://github.com/LucasFranciscoCorreia/DigitDetector/blob/master/readme/Precision Manhattan.png?raw=true" width="1000em">
    <img src="https://github.com/LucasFranciscoCorreia/DigitDetector/blob/master/readme/Precision Euclidean.png?raw=true" width="1000em">
    <img src="https://github.com/LucasFranciscoCorreia/DigitDetector/blob/master/readme/Precision Cosine.png?raw=true" width="1000em">
</div>

<h2>Recall</h2>

The following graphs presents the average precision for each digit for each measure.

<div align="center">
    <img src="https://github.com/LucasFranciscoCorreia/DigitDetector/blob/master/readme/Recall Manhattan.png?raw=true" width="1000em">
    <img src="https://github.com/LucasFranciscoCorreia/DigitDetector/blob/master/readme/Recall Euclidean.png?raw=true" width="1000em">
    <img src="https://github.com/LucasFranciscoCorreia/DigitDetector/blob/master/readme/Recall Cosine.png?raw=true" width="1000em">
</div>

<h2>🛡️ License:</h2>

This project is licensed under the GNU General Public License v3.0