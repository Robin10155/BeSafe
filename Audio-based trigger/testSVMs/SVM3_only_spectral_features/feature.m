function feature(codeMode)
% This file is used to extract features of audio signal 

if(exist("codeMode")==0)
	display("Use either feature(0) for getting training feature or feature (1) for getting testing feature");
	break;
end
if(codeMode == 0)
	clc;
	addpath('../../');
	addpath('/home/jayraj/octaveScripts/voicebox/');
	addpath('/home/jayraj/octaveScripts/audioAnalysisLibraryCode/library/');

	filePath1 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Conversations/Train_Conv/';
	filePath2 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Human Gathering/Train_Gathering/';
	filePath3 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Machinery/Train_Machinery/';
	filePath4 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Multimedia/Train_Multimedia/';
	filePath5 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Outdoors/Train_Outdoors/';
	distressFilePath = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Distress/Train Distress/';
	type = '*.wav';

	[melFeatures1,melLabels1] = melCoefficients(filePath1,type,0);
	melLabels1=melLabels1(1,2:end);

	[melFeatures2,melLabels2] = melCoefficients(filePath2,type,0);
	melLabels2=melLabels2(1,2:end);

	[melFeatures3,melLabels3] = melCoefficients(filePath3,type,0);
	melLabels3=melLabels3(1,2:end);

	[melFeatures4,melLabels4] = melCoefficients(filePath4,type,0);
	melLabels4=melLabels4(1,2:end);

	[melFeatures5,melLabels5] = melCoefficients(filePath5,type,0);
	melLabels5=melLabels5(1,2:end);

	[melFeatures6,melLabels6] = melCoefficients(distressFilePath,type,1);
	melLabels6=melLabels6(1,2:end);

	melFeatures = [melFeatures1; melFeatures2; melFeatures3; melFeatures4; melFeatures5; melFeatures6];
	melLables = [melLabels1, melLabels2, melLabels3, melLabels4, melLabels5, melLabels6];

	save -V6 extractedFeature.mat 'melFeatures' 'melLables';
end

if(codeMode == 1)
	clc;
	addpath('../../');
	addpath('/home/jayraj/octaveScripts/voicebox/');
	addpath('/home/jayraj/octaveScripts/audioAnalysisLibraryCode/library/');

	testDataFilePath = '/home/jayraj/octaveScripts/testSVM/Data/audioFiles/';
	type = '*.wav';
	
	[testFeatures, testLabels] = melCoefficients(testDataFilePath,type,0);

	save -V6 testingFeatures.mat 'testFeatures' 'testLabels';
end
