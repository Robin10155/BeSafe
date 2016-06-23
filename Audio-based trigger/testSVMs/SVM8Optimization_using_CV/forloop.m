function [zz]=forloop(x,y,z)
for i=1:length(x)
	for j = 1: length(y)
		zz(j,i)=z(j+(i-1)*8);
	end
end
end
