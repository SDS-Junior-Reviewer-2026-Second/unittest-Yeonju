package com.calculator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * SUT : Calculator
 * Dependency : Adder -> Mock으로 대체, Stubbing 하여 테스트
 */
@ExtendWith(MockitoExtension.class)
class CalculatorTest {

    @Mock
    Adder adder;

    @InjectMocks
    Calculator calculator;

    @Test
    void addDelegatesDirectlyToAdder() {
        // given
        when(adder.add(3, 4)).thenReturn(7);

        // when
        int result = calculator.add(3, 4);

        // then
        assertThat(result).isEqualTo(7);
        verify(adder, times(1)).add(3, 4);
    }

    @Test
    void subtractCallsAdderWithNegatedB() {
        // given
        when(adder.add(10, -4)).thenReturn(6);

        // when
        int result = calculator.subtract(10, 4);

        // then
        assertThat(result).isEqualTo(6);
        // subtract(10, 4) 내부적으로 adder.add(10, -4) 가 호출되어야 함
        verify(adder, times(1)).add(10, -4);
    }

    /**
     * i = adder.add(i, 1) 에 대한 stub 설정 (i가 1씩 증가)
     * -> 루프 카운터를 증가시킴
     */
    @Test
    void stubbingAdderAddIncrementsLoopCounterByOne() {
        // adder.add(i,1) -> anyInt() + 1 에 stub 걸기
        when(adder.add(anyInt(), eq(1))).thenAnswer(invocation -> {
            int firstArgument = invocation.getArgument(0);
            return firstArgument + 1;
        });

        // anyInt() + 1 에 대한 스텁 검증
        assertThat(adder.add(3, 1)).isEqualTo(4);
        assertThat(adder.add(5, 1)).isEqualTo(6);
        assertThat(adder.add(7, 1)).isEqualTo(8);
    }

    /**
     * adder.add(result, a) 에 대한 stub 설정
     * result 에 a를 누적
     */
    @Test
    void stubbingAdderAddAccumulatesResultWithA() {
        // adder.add(result,a) -> result 에 a 누적하는 코드에 stub 걸기
        when(adder.add(anyInt(), anyInt())).thenAnswer(invocation -> {
            int firstArgument = invocation.getArgument(0);  // result
            int secondArgument = invocation.getArgument(1); // a
            return firstArgument + secondArgument;           // result + a
        });

        // anyInt() + anyInt() 에 대한 스텁 검증
        assertThat(adder.add(3, 2)).isEqualTo(5);
        assertThat(adder.add(10, 3)).isEqualTo(13);
        assertThat(adder.add(7, 1)).isEqualTo(8);
    }

    @Test
    void multiplyUsesOnlyAdderAddToComputeTheProduct() {
        // given
        int a = 4;
        int b = 3;

        // result = adder.add(result, a) -> 결과 누적 (넓은 조건, 먼저 stub)
        when(adder.add(anyInt(), anyInt())).thenAnswer(invocation -> {
            int firstArgument = invocation.getArgument(0);
            int secondArgument = invocation.getArgument(1);
            return firstArgument + secondArgument;
        });

        // i = adder.add(i, 1) -> 루프 카운터 증가 (좁은 조건, 나중에 stub하여 우선 매칭)
        when(adder.add(anyInt(), eq(1))).thenAnswer(invocation -> {
            int firstArgument = invocation.getArgument(0);
            return firstArgument + 1;
        });

        // when
        int result = calculator.multiply(a, b);

        // then
        assertThat(result).isEqualTo(a * b); // 4 * 3 = 12

        // 루프가 b(=3)번 도므로, 두 종류의 add가 각각 3번씩 호출되어야 함
        verify(adder, times(b)).add(anyInt(), eq(1));
        verify(adder, times(b)).add(anyInt(), eq(a));
    }

    @Test
    void multiplyFlipsSignsOfAAndBWhenBIsNegative() {
        // given
        int a = 5;
        int b = -3; // 내부적으로 a=-5, b=3 으로 뒤집힘

        when(adder.add(anyInt(), anyInt())).thenAnswer(invocation -> {
            int firstArgument = invocation.getArgument(0);
            int secondArgument = invocation.getArgument(1);
            return firstArgument + secondArgument;
        });
        when(adder.add(anyInt(), eq(1))).thenAnswer(invocation -> {
            int firstArgument = invocation.getArgument(0);
            return firstArgument + 1;
        });

        // when
        int result = calculator.multiply(a, b);

        // then
        assertThat(result).isEqualTo(a * b); // 5 * -3 = -15
    }

    @Test
    void multiplyNeverCallsAdderAddWhenBIsZero() {
        // when
        int result = calculator.multiply(7, 0);

        // then
        assertThat(result).isZero();
        verifyNoInteractions(adder);
    }
}
