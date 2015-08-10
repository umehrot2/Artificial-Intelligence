import math
import sys
class Naive_Bayes:
	def read_files(self,filename,filename_label):
		with open(filename) as f:
			content = f.readlines()
		self.all_digits = []
		digit = []
		self.label_indexes = []
		self.new_labels = []
		count = 1

		#Read the images from a file into a list
		for line in content:
			if count==28:
				digit.append(line.strip('\n'))
	#	print digit
				self.all_digits.append(digit)
				count = 1
				digit = []
				continue
			digit.append(line.strip('\n'))
			count+=1

		with open(filename_label) as f1:
			labels = f1.readlines()
		self.new_labels = [label.strip('\n') for label in labels[0:]]
		#print self.new_labels	
		self.label_count = len(self.new_labels)

		#FInd the index location corresponding to each class label occurrence
		for val in range(0,10):
		#	print val
			index = [i for i, j in enumerate(self.new_labels) if j == str(val)]
		#	print index
			self.label_indexes.append(index)
		#print self.label_indexes
	
	def test(self,predict_list):
#		print predict_list
		self.all_predictions = []
#		img_iter = 0

		#For each image in the test file
		for img in self.all_digits:
#			if img_iter==5:
#				break
#			img_iter+=1
			self.digit_class_predictions = []

			#For each feature in the image
			for i in range(0,28):
				for j in range(0,28):
					if img[i][j]==' ':
						val = 0
					else:
						val = 1
					count_iter = 0

					#For each digit class add up the log of the posterior probability forund during training
					for obj in predict_list:
						if i==0 and j==0:
							obj.prediction_prob = math.log(obj.prior) 
						#print count_iter
						count_iter+=1
	
						obj.prediction_prob += math.log(obj.prob[(i,j,val)])
						
						#If it is the last feature add all posterior probabilities corresponding to each digit calss to a list
						if i==27 and j==27:
							self.digit_class_predictions.append(obj.prediction_prob)

			#select the digit class with the max posterior probability as the predicted label
			max_prob_value = max(self.digit_class_predictions)

			self.all_predictions.append(self.digit_class_predictions.index(max_prob_value))
	#	print "These are all the predictions"
	#	print self.all_predictions

	def compare_predictions(self):

		count=0

		# compares the predicted label and the actual label
		for i in range(len(self.all_predictions)):
			if str(self.all_predictions[i])==self.new_labels[i]:
				count+=1
		accuracy = (count/float(self.label_count))*100
		#kv.write(str(accuracy)+'\n')
		print "The accuracy is "+str(accuracy)
		self.confusion = [[0 for i in range(0,10)] for j in range(0,10)]

		# compute tht confusion matrix
		for val in range(0,10):
		#	print val
			index = [i for i, j in enumerate(self.new_labels) if j == str(val)]
			den = len(index)
			for ind in index:
				predicted_val = self.all_predictions[ind]
				self.confusion[val][predicted_val]+=1/float(den)
		print "The confusion matrix is \n"
		for i in range(0,10):
			for j in range(0,10):
				print str(self.confusion[i][j])+" "
			print "\n"
		#	print index
		

class predict:
	# Function to train the classifier using the training images and training labels
	def train(self,bayestrain_obj,dig):
		self.prediction_prob = 0
		self.positions = bayestrain_obj.label_indexes[dig]
		self.occurrence_count = len(self.positions)
		self.digit_images = []
		self.prob = {}
		count =1
		k=1
		
		# Find prior probability and add it to the posterior probability for each digit
		self.prior = (self.occurrence_count)/float(bayestrain_obj.label_count)
		self.prediction_prob += math.log(self.prior)
		# gather all the same digits
		for pos in self.positions:
		#	print pos
		#	print bayes_obj.all_digits[pos]
		#	print bayes_obj.all_digits[pos][5]
		#	count +=1
		#	if count==2:
		#		break
			self.digit_images.append(bayestrain_obj.all_digits[pos])

		# Find the posterior probability for each feature of the image
		for i in range(0,28):
			for j in range(0,28):
				count0 = 0
				count1 = 0
				#self.prob[(i,j,0)] = 0
				#self.prob[(i,j,1)] = 0
				for img in self.digit_images:
					if img[i][j]==' ':
						count0+=1
					else:
						count1+=1
				self.prob[(i,j,0)] = (count0+k)/float(self.occurrence_count+(2*k))
				self.prob[(i,j,1)] = (count1+k)/float(self.occurrence_count+(2*k))
#		for key,val in self.prob.items():
#			print key
#			print val

				

if __name__=='__main__':
	bayes_train = Naive_Bayes()
	'''
	training_images_path = '/home/sanjana/AI/digitdata/trainingimages'
	training_labels_path = '/home/sanjana/AI/digitdata/traininglabels'
	test_images_path = '/home/sanjana/AI/digitdata/testimages'
	test_labels_path = '/home/sanjana/AI/digitdata/testlabels'
	'''
	if len(sys.argv) < 4:
		print "Usage: <training image path> <training labels path> <test image path> <test labels path>"
		sys.exit(0)
	
	training_images_path = sys.argv[1]
	training_labels_path = sys.argv[2]
	test_images_path = sys.argv[3]
	test_labels_path = sys.argv[4]

	bayes_train.read_files(training_images_path,training_labels_path)
	digit_object = []
	digit_object = [predict() for i in range(0,10)]
	#k=0
	#Find the accuracy for each value of k and write to a file
	#with open('k_variation1.txt','w') as kv:
		
	  #for k in range(1,50,1):
	 	#kv.write(str(k)+'\t')
	for i in range(0,10):
			digit_object[i].train(bayes_train,i)
	  	#print i
	  	#print digit_object[i].prob
	  	#print digit_object[i].prediction_prob
	bayes_test = Naive_Bayes()
	bayes_test.read_files(test_images_path,test_labels_path)
	bayes_test.test(digit_object)
	bayes_test.compare_predictions()
