function featureMFCC(codeMode)
% This file is used to extract features of audio signal 

if(exist('codeMode')==0)
	display('Use either feature(0) for getting training feature or feature (1) for getting testing feature');
end
if(codeMode == 0)
	clc;
	addpath('/home/jayraj/octaveScripts/');
	addpath('/home/jayraj/octaveScripts/voicebox/')

	%filePath1 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Conversations/Train_Conv/';
	%filePath2 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Human Gathering/Train_Gathering/';
	%filePath3 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Machinery/Train_Machinery/';
	%filePath4 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Multimedia/Train_Multimedia/';
	%filePath5 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Outdoors/Train_Outdoors/';
	%filePath6 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Indoors/Train_Indoors/';
	%distressFilePath = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Distress/Train_Distress/';

	filePath1 = '/home/jayraj/octaveScripts/validationDataset/EvaluationConv/';
	filePath2 = '/home/jayraj/octaveScripts/validationDataset/EvaluationGathering/';
	filePath3 = '/home/jayraj/octaveScripts/validationDataset/EvaluationMachinery/';
	filePath4 = '/home/jayraj/octaveScripts/validationDataset/EvaluationMultimedia/';
	filePath5 = '/home/jayraj/octaveScripts/validationDataset/EvaluationOutdoors/';
	filePath6 = '/home/jayraj/octaveScripts/validationDataset/EvaluationIndoors/';
	distressFilePath = '/home/jayraj/octaveScripts/validationDataset/EvaluationDistress/';

	type = '*.wav';

	[melFeatures1,melLabels1] = melCoefficients(filePath1,type,-1);
	melLabels1=melLabels1(1,2:end);

	[melFeatures2,melLabels2] = melCoefficients(filePath2,type,-1);
	melLabels2=melLabels2(1,2:end);

	[melFeatures3,melLabels3] = melCoefficients(filePath3,type,-1);
	melLabels3=melLabels3(1,2:end);

	[melFeatures4,melLabels4] = melCoefficients(filePath4,type,-1);
	melLabels4=melLabels4(1,2:end);

	[melFeatures5,melLabels5] = melCoefficients(filePath5,type,-1);
	melLabels5=melLabels5(1,2:end);

	[melFeatures6,melLabels6] = melCoefficients(filePath6,type,-1);
	melLabels6=melLabels6(1,2:end);

	[melFeatures7,melLabels7] = melCoefficients(distressFilePath,type,1);
	melLabels7=melLabels7(1,2:end);

	melFeatures = [melFeatures1; melFeatures2; melFeatures3; melFeatures4; melFeatures5; melFeatures6; melFeatures7];
	melLables = [melLabels1, melLabels2, melLabels3, melLabels4, melLabels5, melLabels6, melLabels7];

	%save -V6 trainingMFCCFeatureSet.mat 'melFeatures' 'melLables';
	save -V6 CVMFCCFeatureSet.mat 'melFeatures' 'melLables';
end

if(codeMode == 1)
	clc;
	addpath('../../');
	addpath('/home/jayraj/octaveScripts/voicebox/')

	testDataFilePath = '/home/jayraj/octaveScripts/testSVM/Data/audioFiles/';
	type = '*.wav';
	
	[testFeatures, testLabels] = melCoefficients(testDataFilePath,type,0);

	save -V6 testingFeatures.mat 'testFeatures' 'testLabels';
end
