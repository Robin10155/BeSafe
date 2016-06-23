%this function returns computed MFCC coeficients for individual file

function [featureVector,labels] = featureExtrMelCoefficients(audio,fs,audioname,vectorClass)

if(length(audio) < 0.3*fs)
	featureVector = [];
	labels = [];
	return;
end

% making sure that audio vector is row alligned
[ar,ac] = size(audio);	
if(ar<ac)
	audio = audio';
end

% convert to mono if stereo
if(size(audio,2)==2)
	audio = sum(audio,2); 		% perform row wise sum
end
idx = find(audio);
audio = audio(idx(1):end);

% find number of 2 sec samples
sig_len = length(audio);
samp_len = 1.98*fs;
overlap = samp_len;
nSamples= floor(sig_len/overlap);	% this will initialise nSample to the number of
					% 2 sec samples in that wav file

%initialize variables
featureVector = [];
labels = [];	
feat_count = 1;

if (sig_len <= samp_len)
	
	[segments, fs] = detectVoiced(audio,fs);
	if(isempty(segments))
		featureVector = [];
		labels = [];
		return;
	end
	segSig = [];
	for i = 1:length(segments)
		segSig = [segSig; segments{i}];
	end
	if(size(segSig,2)~=1)
		disp(size(audio));
		disp(size(segSig));
		disp(size(segments));
		error('segSig error');
	end
	audio = segSig;
	
	mfc_vec = feature_extr_mfcc(audio,fs);

	featureVector(feat_count,:)= [mfc_vec,vectorClass];
	labels{feat_count} = strcat(audioname);
	return;
end

samp1 = 1;
samp2 = samp_len;		% remember samp_len is set to 1.98*fs on top
for for_all_samples = 1: nSamples
	sample = audio(samp1:samp2);

	[segments,fs] = detectVoiced(sample,fs);
	if(isempty(segments))
		samp1 = samp1 + overlap;
		samp2 = samp2 + overlap;
		if(samp2 > sig_len)
			if(samp1 > (sig_len-samp_len/2))
				break;
			else
				samp2 = sig_len;	
			end
		end
		continue;
	end
	segSig =[];
	for i =1: length(segments)
		segSig = [segSig; segments{i}];
	end
	if(size(segSig,2)~=1)
        	disp(size(segSig)); 
        	disp(size(segments));
        	error('segSig error');
   	end
    	sample = segSig;
	
	mfc_vec = feature_extr_mfcc(sample,fs);

	featureVector(feat_count,:)= [mfc_vec,vectorClass];
	labels{feat_count} = strcat(audioname,'_',num2str(for_all_samples));
	feat_count = feat_count +1;

	samp1 = samp1 + overlap;
	samp2 = samp2 + overlap;
	if(samp2 > sig_len)
		if(samp1 > (sig_len-samp_len/2))
			break;
		else
			samp2 = sig_len;
		end
	end
end	


%-----------------------------------------------------------------------
%extract MFCC feature for single audio
%-----------------------------------------------------------------------
function mfcc_vector = feature_extr_mfcc(sample,fs)
mfc = melcepst(sample,fs,'0');
mfc(any(mfc == 0,2),:) = [];
mfcc_vector = [mean(mfc,1), std(mfc,0,1)];%std(x,opt,dim) --->opt- 0: normalize with N-1
					  %	   		   1: normalize with N
