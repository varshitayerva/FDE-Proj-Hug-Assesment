const express = require('express');
const app = express();

app.use(express.json());

app.use((req, res, next) => {
  if (req.method === 'POST') {
    const prompt = req.body.inputs || '';

    let response = '';

    if (prompt.includes('Analyze the fashion style')) {
      response = 'Style Analysis: Based on your profile characteristics, your fashion sense aligns well with contemporary minimalist trends. You have a strong eye for color coordination and your preferred styles complement your body type effectively. Your confidence in fashion choices is evident, and your style demonstrates maturity and sophistication. Overall style match score: 82 out of 100.';
    } else if (prompt.includes('outfit')) {
      response = 'Outfit Recommendations:\n\n1. Business Casual: Pair a crisp white oxford shirt with well-fitted neutral chinos and loafers.\n2. Weekend Casual: Wear a quality T-shirt with dark jeans and clean sneakers.\n3. Evening Look: A tailored dress or smart trousers with an elegant top.\n4. Smart Casual: Mix a patterned button-up with chinos and dress shoes.';
    } else if (prompt.includes('Summarize')) {
      response = 'Fashion Summary: Your personal style is characterized by a preference for clean lines and sophisticated color palettes. Focus on quality basics that you can mix and match, and invest in key pieces like a well-fitted blazer or classic shoes. Your current style foundation is strong.';
    } else {
      response = 'Mock AI response for your fashion profile.';
    }

    return res.json([{
      generated_text: response
    }]);
  }
  next();
});

app.listen(3001, () => {
  console.log('Mock AI Server running on http://localhost:3001');
});
