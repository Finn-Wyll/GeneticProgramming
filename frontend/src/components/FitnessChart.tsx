import React from 'react';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer
} from 'recharts';

interface FitnessChartProps {
  data: Array<{
    generation: number;
    bestFitness: number;
    averageFitness: number;
  }>;
}

export default function FitnessChart({ data }: FitnessChartProps) {
  return (
    <div className="h-[300px] w-full">
      <ResponsiveContainer width="100%" height="100%">
        <LineChart
          data={data}
          margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
        >
          <CartesianGrid strokeDasharray="3 3" vertical={false} />
          <XAxis 
            dataKey="generation" 
            label={{ value: 'Generation', position: 'insideBottomRight', offset: -5 }} 
          />
          <YAxis 
            domain={[0, 1]} 
            label={{ value: 'Fitness', angle: -90, position: 'insideLeft' }} 
          />
          <Tooltip 
            contentStyle={{ borderRadius: '8px', border: 'none', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)' }}
          />
          <Legend verticalAlign="top" height={36} />
          <Line
            type="monotone"
            dataKey="bestFitness"
            name="Best Fitness"
            stroke="#2563eb"
            strokeWidth={3}
            dot={false}
            activeDot={{ r: 8 }}
          />
          <Line
            type="monotone"
            dataKey="averageFitness"
            name="Average Fitness"
            stroke="#94a3b8"
            strokeWidth={2}
            dot={false}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
