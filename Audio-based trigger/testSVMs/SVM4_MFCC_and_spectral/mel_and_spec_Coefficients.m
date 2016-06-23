function [melFeatures,melLabels] = mel_and_spec_Coefficients(filePath,type,vectorClass)
files = dir(fullfile(filePath,type));
fprintf('inside mel_and_spec_coefficients\n');
fprintf('Number of .wav file being processed: %d\n',length(files));
melFeatures=[];
melLabels=[];
for i = 1:length(files)
	filename = files(i).name;
	%display(filename);
	[audio,fs]=wavread([filePath,filename]);
	
	[feat,lab] = featureExtrMelCoefficients_and_spec(audio,fs,filename(1:end-4),vectorClass);

	melFeatures = [melFeatures; feat];
	melLabels = [melLabels, lab];
end
