function feature(codeMode)
% This file is used to extract features of audio signal 

if(exist('codeMode')==0)
	display('Use either feature(0) for getting training feature or feature (1) for getting testing feature');
end
if(codeMode == 0)
	clc;
	addpath('../../');
    addpath('/home/jayraj/octaveScripts/');
	addpath('/home/jayraj/octaveScripts/voicebox/');
	addpath('/home/jayraj/octaveScripts/audioAnalysisLibraryCode/library/');

	filePath1 = '/home/jayraj/octaveScripts/validationDataset/EvaluationConv/';
	%filePath2 = '/home/jayraj/octaveScripts/validationDataset/EvaluationGathering/';
	filePath3 = '/home/jayraj/octaveScripts/validationDataset/EvaluationMachinery/';
	filePath4 = '/home/jayraj/octaveScripts/validationDataset/EvaluationMultimedia/';
	filePath5 = '/home/jayraj/octaveScripts/validationDataset/EvaluationOutdoors/';
	distressFilePath = '/home/jayraj/octaveScripts/validationDataset/EvaluationDistress/';
	type = '*.wav';

	[melFeatures1,melLabels1] = mel_and_spec_Coefficients(filePath1,type,-1);
	melLabels1=melLabels1(1,2:end);
	
% 	save -v6 extFeature1.mat 'melFeatures1' 'melLabels1';
% 	disp('feature1 done');
% 	fflush(stdout);

% 	[melFeatures2,melLabels2] = mel_and_spec_Coefficients(filePath2,type,-1);
% 	melLabels2=melLabels2(1,2:end);

% 	save -v6 extFeature2.mat 'melFeatures2' 'melLabels2';
% 	disp('feature2 done');
% 	fflush(stdout);

	[melFeatures3,melLabels3] = mel_and_spec_Coefficients(filePath3,type,-1);
	melLabels3=melLabels3(1,2:end);

% 	save -v6 extFeature3.mat 'melFeatures3' 'melLabels3';
% 	disp('feature3 done');
% 	fflush(stdout);

	[melFeatures4,melLabels4] = mel_and_spec_Coefficients(filePath4,type,-1);
	melLabels4=melLabels4(1,2:end);

% 	save -v6 extFeature4.mat 'melFeatures4' 'melLabels4';
% 	disp('feature4 done');
% 	fflush(stdout);

	[melFeatures5,melLabels5] = mel_and_spec_Coefficients(filePath5,type,-1);
	melLabels5=melLabels5(1,2:end);

% 	save -v6 extFeature5.mat 'melFeatures5' 'melLabels5';
% 	disp('feature5 done');
% 	fflush(stdout);
% 
	[melFeatures6,melLabels6] = mel_and_spec_Coefficients(distressFilePath,type,1);
	melLabels6=melLabels6(1,2:end);

% 	save -v6 extFeature6.mat 'melFeatures6' 'melLabels6';
% 	disp('feature6 done');
% 	fflush(stdout);

	melFeatures = [melFeatures1; melFeatures3; melFeatures4; melFeatures5; melFeatures6];
	melLables = [melLabels1, melLabels3, melLabels4, melLabels5, melLabels6];

	save -V6 extractedMelAndSpecFeature.mat 'melFeatures' 'melLables';
end

if(codeMode == 1)
	display('inside feature(1)');
	fflush(stdout);
	addpath('../../');
	addpath('/home/jayraj/octaveScripts/voicebox/');
	addpath('/home/jayraj/octaveScripts/audioAnalysisLibraryCode/library/');

	testDataFilePath = '/home/jayraj/octaveScripts/testSVM/Data/audioFiles/';
	type = '*.wav';
	
	[testFeatures, testLabels] = mel_and_spec_Coefficients(testDataFilePath,type,0);

	save -V6 testingMFCCAndSpecFeatures.mat 'testFeatures' 'testLabels';
end
