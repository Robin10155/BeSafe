function featureMFCC(codeMode)
% This file is used to extract features of audio signal 

if(exist('codeMode')==0)
	display('Use either feature(0) for getting training feature or feature (1) for getting testing feature');
end
if(codeMode == 0)
	clc;
    addpath('/home/jayraj/octaveScripts/audioAnalysisLibraryCode/library/');
	addpath('/home/jayraj/octaveScripts/');
	addpath('/home/jayraj/octaveScripts/voicebox/')

	filePath1 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Conversations/Train_Conv/';
	filePath2 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Human Gathering/Train_Gathering/';
	filePath3 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Machinery/Train_Machinery/';
	filePath4 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Multimedia/Train_Multimedia/';
	filePath5 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Outdoors/Train_Outdoors/';
	filePath6 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Indoors/Train_Indoors/';
    %filePath7 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/yourDataset/conversation/train/';
    %filePath8 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/yourDataset/crowd/train/';
    %filePath9 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/yourDataset/multimedia/train/';
    %filePath10 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/yourDataset/outdoors/train/';
	distressFilePath = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/Distress/Train_Distress/';
    %distressFilePath2 = '/home/jayraj/octaveScripts/testSVM/Data/usedDataSet/yourDataset/distress/train/';

	%filePath1 = '/home/jayraj/octaveScripts/validationDataset/EvaluationConv/';
	%filePath2 = '/home/jayraj/octaveScripts/validationDataset/EvaluationGathering/';
	%filePath3 = '/home/jayraj/octaveScripts/validationDataset/EvaluationMachinery/';
	%filePath4 = '/home/jayraj/octaveScripts/validationDataset/EvaluationMultimedia/';
	%filePath5 = '/home/jayraj/octaveScripts/validationDataset/EvaluationOutdoors/';
	%filePath6 = '/home/jayraj/octaveScripts/validationDataset/EvaluationIndoors/';
	%distressFilePath = '/home/jayraj/octaveScripts/validationDataset/EvaluationDistress/';

	type = '*.wav';

	[melFeatures1,melLabels1] = mel_and_spec_Coefficients(filePath1,type,-1);
	melLabels1=melLabels1(1,2:end);

	[melFeatures2,melLabels2] = mel_and_spec_Coefficients(filePath2,type,-1);
	melLabels2=melLabels2(1,2:end);

	[melFeatures3,melLabels3] = mel_and_spec_Coefficients(filePath3,type,-1);
	melLabels3=melLabels3(1,2:end);

	[melFeatures4,melLabels4] = mel_and_spec_Coefficients(filePath4,type,-1);
	melLabels4=melLabels4(1,2:end);

	[melFeatures5,melLabels5] = mel_and_spec_Coefficients(filePath5,type,-1);
	melLabels5=melLabels5(1,2:end);

	[melFeatures6,melLabels6] = mel_and_spec_Coefficients(filePath6,type,-1);
	melLabels6=melLabels6(1,2:end);

% 	[melFeatures7,melLabels7] = mel_and_spec_Coefficients(filePath7,type,-1);
% 	melLabels7=melLabels7(1,2:end);
%     
%     [melFeatures8,melLabels8] = mel_and_spec_Coefficients(filePath8,type,-1);
% 	melLabels8=melLabels8(1,2:end);
%     
%     [melFeatures9,melLabels9] = mel_and_spec_Coefficients(filePath9,type,-1);
% 	melLabels9=melLabels9(1,2:end);
%     
%     [melFeatures10,melLabels10] = mel_and_spec_Coefficients(filePath10,type,-1);
% 	melLabels10=melLabels10(1,2:end);  
    
    [melFeatures11,melLabels11] = mel_and_spec_Coefficients(distressFilePath,type,1);
	melLabels11=melLabels11(1,2:end);
    
%     [melFeatures12,melLabels12] = mel_and_spec_Coefficients(distressFilePath2,type,1);
% 	melLabels12=melLabels12(1,2:end);

%	melFeatures = [melFeatures1; melFeatures2; melFeatures3; melFeatures4; melFeatures5; melFeatures6; melFeatures7; melFeatures8; melFeatures9; melFeatures10; melFeatures11; melFeatures12];
%	melLables = [melLabels1, melLabels2, melLabels3, melLabels4, melLabels5, melLabels6, melLabels7, melLabels8, melLabels9, melLabels10, melLabels11, melLabels12];

    melFeatures = [melFeatures1; melFeatures2; melFeatures3; melFeatures4; melFeatures5; melFeatures6; melFeatures11];
	melLables = [melLabels1, melLabels2, melLabels3, melLabels4, melLabels5, melLabels6, melLabels11];
    
	str = ['/home/jayraj/octaveScripts/testSVM/Mel_plus_spec_FeatureSet/smalldataset/','trainMFCC_and_specFeatureSet.mat'];
	save('-V6',str,'melFeatures','melLables');
end

if(codeMode == 1)
	clc;
	addpath('../../');
	addpath('/home/jayraj/octaveScripts/voicebox/')

	testDataFilePath = '/home/jayraj/octaveScripts/testSVM/Data/audioFiles/';
	type = '*.wav';
	
	[testFeatures, testLabels] = mel_and_spec_Coefficients(testDataFilePath,type,0);

	save -V6 testingFeatures.mat 'testFeatures' 'testLabels';
end
