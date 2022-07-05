from rest_framework import serializers
from .models import CaptureImage


class PostSerializer(serializers.ModelSerializer):
   category = serializers.CharField(max_length=15)
   commodity_name = serializers.CharField(max_length=20)
   commodity_variety = serializers.CharField(max_length=20)
#    commodity_shape = serializers.CharField(max_length=15)
#    damage = serializers.IntegerField()
   commodity_size = serializers.IntegerField()
   commodity_defect_type = serializers.CharField(max_length=20)
   commodity_defect_value = serializers.IntegerField()

   class Meta:
      model = CaptureImage
      # fields = ['img']
      fields = ('img','category','commodity_name','commodity_variety','commodity_size','commodity_defect_type','commodity_defect_value')
    
class TestSerializer(serializers.ModelSerializer):
   category = serializers.CharField(max_length=15)
   commodity_name = serializers.CharField(max_length=20)
   commodity_variety = serializers.CharField(max_length=20)
   commodity_size = serializers.IntegerField()
   commodity_defect_type = serializers.CharField(max_length=20)
   commodity_defect_value = serializers.IntegerField()
   class Meta:
         model = CaptureImage
         fields = ['img_id','img','category','commodity_name','commodity_variety','commodity_size','commodity_defect_type','commodity_defect_value']