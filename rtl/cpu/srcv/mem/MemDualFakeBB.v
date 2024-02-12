`include "../Config.v"

module MemDualFakeBB(
    input  wire                       pMem_bRdEn,
    input  wire [`ADDR_WIDTH - 1 : 0] pMem_bRdAddrA,
    input  wire [`ADDR_WIDTH - 1 : 0] pMem_bRdAddrB,
    input  wire                       pMem_bWrEn,
    input  wire [`ADDR_WIDTH - 1 : 0] pMem_bWrAddr,
    input  wire [`DATA_WIDTH - 1 : 0] pMem_bWrData,
    input  wire                       pMem_bWrMask_0,
    input  wire                       pMem_bWrMask_1,
    input  wire                       pMem_bWrMask_2,
    input  wire                       pMem_bWrMask_3,

    output reg  [`INST_WIDTH - 1 : 0] pMem_bRdDataA,
    output reg  [`DATA_WIDTH - 1 : 0] pMem_bRdDataB
);

    import "DPI-C" context function int unsigned readSimInstData(
        input int unsigned addr,
        input byte unsigned len);
    import "DPI-C" context function int unsigned readSimMemoryData(
        input int unsigned addr,
        input byte unsigned len);
    import "DPI-C" context function void writeSimMemoryData(
        input int unsigned addr,
        input int unsigned data,
        input byte unsigned len);

    always @(pMem_bRdAddrA) begin
        if (pMem_bRdEn) begin
            pMem_bRdDataA = readSimInstData(pMem_bRdAddrA, 4);
        end
    end

    always @(pMem_bRdAddrB) begin
        if (pMem_bRdEn) begin
            pMem_bRdDataB = readSimMemoryData(pMem_bRdAddrB, 4);
        end
    end

    wire [3: 0] pMem_bWrMask = { pMem_bWrMask_0,
                                 pMem_bWrMask_1,
                                 pMem_bWrMask_2,
                                 pMem_bWrMask_3 };
    always @(pMem_bWrAddr or pMem_bWrData) begin
        if (pMem_bWrEn) begin
            case (pMem_bWrMask)
                4'b0001: writeSimMemoryData(pMem_bWrAddr, pMem_bWrData, 1);
                4'b0011: writeSimMemoryData(pMem_bWrAddr, pMem_bWrData, 2);
                4'b1111: writeSimMemoryData(pMem_bWrAddr, pMem_bWrData, 4);
                default: writeSimMemoryData(pMem_bWrAddr, pMem_bWrData, 4);
            endcase
        end
    end

endmodule
