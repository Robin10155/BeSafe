addpath('/home/jayraj/octaveScripts/libsvm-3.17/matlab/');
load("extractedFeature.mat");
display(size(melFeatures));
Labels = melFeatures(:,79);
melFeatures = melFeatures(:,2:78);

%training SVM with default settings 
model = svmtrain(Labels,melFeatures);

feature(1);
load("testingFeatures.mat");
display(size(testFeatures));
testFeatures=testFeatures(:,2:78);
svmpredict(zeros(size(testFeatures,1),1),testFeatures,model)
