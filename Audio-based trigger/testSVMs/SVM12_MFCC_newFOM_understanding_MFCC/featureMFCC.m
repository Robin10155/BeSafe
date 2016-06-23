function featureMFCC(codeMode)
% This file is used to extract features of audio signal 

if(exist('codeMode')==0)
	display('Use either feature(0) for getting training feature or feature (1) for getting testing feature');
end
if(codeMode == 0)
	clc;
	addpath('/home/jayraj/octaveScripts/');
	addpath('/home/jayraj/octaveScripts/voicebox/')

% 	filePath1 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Conversations/Train_Conv/';
% 	filePath2 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Human Gathering/Train_Gathering/';
% 	filePath3 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Machinery/Train_Machinery/';
% 	filePath4 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Multimedia/Train_Multimedia/';
% 	filePath5 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Outdoors/Train_Outdoors/';
% 	filePath6 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Indoors/Train_Indoors/';
%     filePath7 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/yourDataset/conversation/train/';
%     filePath8 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/yourDataset/multimedia/train/';
%     filePath9 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/yourDataset/outdoors/train/';
% 	distressFilePath = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Distress/Train_Distress/';
%     distressFilePath2 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/yourDataset/distress/train/';

	filePath1 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Conversations/Evaluation_Conv/';
	filePath2 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Human Gathering/Evaluation_Gathering/';
	filePath3 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Machinery/Evaluation_Machinery/';
	filePath4 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Multimedia/Evaluation_Multimedia/';
	filePath5 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Outdoors/Evaluation_Outdoors/';
	filePath6 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Indoors/Evaluation_Indoors/';
    filePath7 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/yourDataset/conversation/validation/';
    filePath8 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/yourDataset/multimedia/validation/';
    filePath9 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/yourDataset/outdoors/validation/';
	distressFilePath = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Distress/Evaluation_Distress/';
    distressFilePath2 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/yourDataset/distress/validation/';

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

	[melFeatures7,melLabels7] = melCoefficients(filePath7,type,-1);
	melLabels7=melLabels7(1,2:end);
    
    [melFeatures8,melLabels8] = melCoefficients(filePath8,type,-1);
	melLabels8=melLabels8(1,2:end);
    
    [melFeatures9,melLabels9] = melCoefficients(filePath9,type,-1);
	melLabels9=melLabels9(1,2:end);
    
    [melFeatures10,melLabels10] = melCoefficients(distressFilePath,type,1);
	melLabels10=melLabels10(1,2:end);
    
    [melFeatures11,melLabels11] = melCoefficients(distressFilePath2,type,1);
 	melLabels11=melLabels11(1,2:end);
    
	melFeatures = [melFeatures1; melFeatures2; melFeatures3; melFeatures4; melFeatures5; melFeatures6; melFeatures7; melFeatures8; melFeatures9; melFeatures10; melFeatures11];
	melLables = [melLabels1, melLabels2, melLabels3, melLabels4, melLabels5, melLabels6, melLabels7, melLabels8, melLabels9, melLabels10, melLabels11];

	str = ['/home/jayraj/octaveScripts/testSVM/MelFeatureSets/big_set/CV/','CVBigMFCCFeatureSet.mat'];
	save('-V6',str,'melFeatures','melLables');
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
