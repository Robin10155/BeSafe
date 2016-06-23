function [melFeatures,melLabels] = melCoefficients(filePath,type,vectorClass);
files = dir(fullfile(filePath,type));
fprintf('Number of .wav file being processed: %d\n',length(files));
%fflush(stdout);
melFeatures=[];
melLabels=[];
for i = 1:length(files)
	filename = files(i).name;
    %fprintf('Name of wav file: %s\n', filename);
	[audio,fs]=wavread([filePath,filename]);
	[feat,lab] = featureExtrMelCoefficients(audio,fs,filename(1:end-4),vectorClass);

	melFeatures = [melFeatures; feat];
	melLabels = [melLabels, lab];
end
