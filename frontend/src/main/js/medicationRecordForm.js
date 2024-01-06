import { createApp } from 'vue';
import TakenMedicineInputField from '@main/js/components/medicationrecord/TakenMedicineInputField.vue';
import ResultMessage from '@main/js/components/ResultMessage.vue';

createApp({
  components: {
    'taken-medicine': TakenMedicineInputField,
    'result-message': ResultMessage,
  },
  data() {
    return {
      effectOptions: [
        '頭痛',
        '発熱',
        'のどの痛み',
        '鼻づまり',
        '咳',
        '嘔吐',
        '下痢',
        '腹痛',
        '便秘',
        '食欲不振',
        '疲労感',
        '関節痛',
        '筋肉痛',
        '動悸',
        '呼吸困難',
        '皮膚の発疹',
        '目のかゆみ',
        '耳の痛み',
        '頻尿',
        '眠れない',
      ],
    };
  },
  methods: {
    goBack() {
      window.history.back();
    },
    activateResultMessage(type, message, details) {
      this.$refs.resultMessage.activate(type, message, details);
    },
  },
  provide() {
    return {
      activateResultMessage: this.activateResultMessage,
    };
  },
}).mount('#medicationRecordForm');